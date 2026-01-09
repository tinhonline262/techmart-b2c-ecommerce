package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import com.shopping.microservices.inventory_service.event.InventoryReservedEvent;
import com.shopping.microservices.inventory_service.event.OrderCreatedEvent;
import com.shopping.microservices.inventory_service.repository.InventoryRepository;
import com.shopping.microservices.inventory_service.repository.ReservedOrderRepository;
import com.shopping.microservices.inventory_service.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing inventory reservations via Kafka events
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryReservationServiceImpl implements InventoryReservationService {

    private final ReservedOrderRepository reservedOrderRepository;
    private final InventoryRepository inventoryRepository;

    private static final int DEFAULT_EXPIRATION_MINUTES = 30;


    /**
     * Process order reservation from OrderCreatedEvent
     * Returns true if all items reserved successfully, false otherwise
     */
    @Transactional
    public boolean processOrderReservation(OrderCreatedEvent event) {
        log.info("Processing reservation for order: {}, items: {}",
                event.orderNumber(), event.orderItems().size());

        List<ReservedOrder> reservations = new ArrayList<>();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES);

        try {
            // Process each order item
            for (OrderCreatedEvent.OrderItemData item : event.orderItems()) {
                // Find inventory by SKU
                Inventory inventory = inventoryRepository.findBySku(item.sku())
                        .orElseThrow(() -> new RuntimeException("Product not found with SKU: " + item.sku()));

                // Check if enough quantity is available
                long availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
                if (availableQuantity < item.quantity()) {
                    log.warn("Insufficient inventory for SKU: {}. Available: {}, Requested: {}",
                            item.sku(), availableQuantity, item.quantity());

                    // Rollback: release previously reserved items
                    rollbackReservations(reservations);
                    return false;
                }

                // Update inventory reserved quantity
                inventory.setReservedQuantity(inventory.getReservedQuantity() + item.quantity());
                inventoryRepository.save(inventory);

                // Create reservation record
                ReservedOrder reservation = ReservedOrder.builder()
                        .orderId(event.orderNumber())
                        .productId(item.productId())
                        .sku(item.sku())
                        .warehouseId(inventory.getWarehouseId())
                        .reservedQuantity(item.quantity().longValue())
                        .status(ReservedOrder.ReservationStatus.RESERVED)
                        .expiresAt(expiresAt)
                        .build();

                ReservedOrder savedReservation = reservedOrderRepository.save(reservation);
                reservations.add(savedReservation);

                log.info("Reserved {} units of SKU: {} for order: {}",
                        item.quantity(), item.sku(), event.orderNumber());
            }

            log.info("Successfully reserved all items for order: {}", event.orderNumber());
            return true;

        } catch (Exception e) {
            log.error("Error processing reservation for order: {}", event.orderNumber(), e);
            rollbackReservations(reservations);
            return false;
        }
    }

    /**
     * Rollback reservations in case of failure
     */
    private void rollbackReservations(List<ReservedOrder> reservations) {
        for (ReservedOrder reservation : reservations) {
            try {
                Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                        .orElse(null);
                if (inventory != null) {
                    inventory.setReservedQuantity(
                            inventory.getReservedQuantity() - reservation.getReservedQuantity());
                    inventoryRepository.save(inventory);
                }
                reservedOrderRepository.delete(reservation);
            } catch (Exception e) {
                log.error("Error rolling back reservation: {}", reservation.getId(), e);
            }
        }
    }

    /**
     * Build InventoryReservedEvent from successful reservation
     */
    public InventoryReservedEvent buildReservedEvent(OrderCreatedEvent orderEvent) {
        List<ReservedOrder> reservations = reservedOrderRepository
                .findByOrderId(orderEvent.orderNumber());

        List<InventoryReservedEvent.ReservedItem> reservedItems = reservations.stream()
                .map(r -> InventoryReservedEvent.ReservedItem.builder()
                        .productId(r.getProductId())
                        .sku(r.getSku())
                        .quantity(r.getReservedQuantity().intValue())
                        .build())
                .collect(Collectors.toList());

        return InventoryReservedEvent.builder()
                .orderId(orderEvent.orderId())
                .orderNumber(orderEvent.orderNumber())
                .reservedItems(reservedItems)
                .message("Inventory reserved successfully")
                .build();
    }

    /**
     * Confirm a reservation (order was completed successfully)
     */
    @Transactional
    public void confirmReservation(String orderId) {
        List<ReservedOrder> reservations = reservedOrderRepository.findByOrderIdAndStatus(
                orderId, ReservedOrder.ReservationStatus.RESERVED);

        for (ReservedOrder reservation : reservations) {
            // Update inventory - deduct from both quantity and reserved quantity
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            inventory.setQuantity(inventory.getQuantity() - reservation.getReservedQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
            inventoryRepository.save(inventory);

            // Update reservation status
            reservation.setStatus(ReservedOrder.ReservationStatus.CONFIRMED);
            reservedOrderRepository.save(reservation);

            log.info("Confirmed reservation for order: {}, product: {}", orderId, reservation.getProductId());
        }
    }

    /**
     * Cancel a reservation (order was cancelled or failed)
     */
    @Transactional
    public void cancelReservation(String orderId) {
        List<ReservedOrder> reservations = reservedOrderRepository.findByOrderIdAndStatus(
                orderId, ReservedOrder.ReservationStatus.RESERVED);
        if (reservations.isEmpty()) {
            log.info("No active reservations found for order: {}", orderId);
            return;
        }
        for (ReservedOrder reservation : reservations) {
            // Release reserved quantity back to available inventory
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
            inventoryRepository.save(inventory);

            // Update reservation status
            reservation.setStatus(ReservedOrder.ReservationStatus.CANCELLED);
            reservedOrderRepository.save(reservation);

            log.info("Cancelled reservation for order: {}, product: {}", orderId, reservation.getProductId());
        }
    }

    /**
     * Process expired reservations
     */
    @Transactional
    public void processExpiredReservations() {
        log.info("Processing expired reservations at {}", LocalDateTime.now());
        List<ReservedOrder> expiredReservations = reservedOrderRepository.findExpiredReservations(LocalDateTime.now());

        for (ReservedOrder reservation : expiredReservations) {
            // Release reserved quantity
            Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
            inventoryRepository.save(inventory);

            // Update reservation status to expired
            reservation.setStatus(ReservedOrder.ReservationStatus.EXPIRED);
            reservedOrderRepository.save(reservation);

            log.info("Expired reservation for order: {}, product: {}", reservation.getOrderId(), reservation.getProductId());
        }

        if (!expiredReservations.isEmpty()) {
            log.info("Processed {} expired reservations", expiredReservations.size());
        }
        log.info("Finished processing expired reservations");
    }


    /**
     * Clean up old reservations (confirmed, cancelled, expired)
     */
    @Transactional
    public int cleanupOldReservations(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deletedCount = reservedOrderRepository.deleteOldReservations(cutoffDate);
        log.info("Cleaned up {} old reservations older than {} days", deletedCount, daysOld);
        return deletedCount;
    }
}

