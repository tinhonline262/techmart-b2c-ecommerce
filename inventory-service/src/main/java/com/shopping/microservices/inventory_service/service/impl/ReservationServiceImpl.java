package com.shopping.microservices.inventory_service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.InventoryTransaction;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import com.shopping.microservices.inventory_service.entity.Warehouse;
import com.shopping.microservices.inventory_service.repository.InventoryRepository;
import com.shopping.microservices.inventory_service.repository.InventoryTransactionRepository;
import com.shopping.microservices.inventory_service.repository.ReservedOrderRepository;
import com.shopping.microservices.inventory_service.repository.WarehouseRepository;
import com.shopping.microservices.inventory_service.service.ReservationService;
import jakarta.inject.Qualifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final InventoryRepository inventoryRepository;
    private final ReservedOrderRepository reservedOrderRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final EventPublisher eventPublisher;



    private static final String SERVICE_NAME = "inventory-service";
    private static final int RESERVATION_EXPIRY_MINUTES = 30;

    @Override
    @Transactional
    public void reserveInventory(OrderEvent orderEvent) {
        log.info("Processing inventory reservation for order: {}", orderEvent.getOrderNumber());

        List<ReservedOrder> reservations = new ArrayList<>();
        List<InventoryEvent.ReservationData> reservationDataList = new ArrayList<>();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESERVATION_EXPIRY_MINUTES);

        try {
            for (OrderEvent.OrderItemData item : orderEvent.getItems()) {
                // Lock inventory record for concurrent access
                Inventory inventory = inventoryRepository.findBySkuWithLock(item.getSku())
                        .orElseThrow(() -> new RuntimeException("Product not found: " + item.getSku()));

                long availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();

                if (availableQuantity < item.getQuantity()) {
                    log.warn("Insufficient inventory for SKU: {}. Available: {}, Requested: {}",
                            item.getSku(), availableQuantity, item.getQuantity());

                    rollbackReservations(reservations);
                    publishInsufficientEvent(orderEvent, item, availableQuantity);
                    return;
                }

                // Increment reserved quantity
                inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
                inventoryRepository.save(inventory);

                // Create reservation record
                ReservedOrder reservation = ReservedOrder.builder()
                        .orderId(orderEvent.getOrderNumber())
                        .productId(item.getProductId())
                        .sku(item.getSku())
                        .warehouseId(inventory.getWarehouseId())
                        .reservedQuantity(item.getQuantity().longValue())
                        .status(ReservedOrder.ReservationStatus.RESERVED)
                        .expiresAt(expiresAt)
                        .build();

                reservedOrderRepository.save(reservation);
                reservations.add(reservation);

                // Build reservation data for event
                String warehouseName = warehouseRepository.findById(inventory.getWarehouseId())
                        .map(Warehouse::getName).orElse("Unknown");

                reservationDataList.add(InventoryEvent.ReservationData.builder()
                        .productId(item.getProductId())
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .warehouseId(inventory.getWarehouseId())
                        .warehouseName(warehouseName)
                        .availableQuantity(availableQuantity - item.getQuantity())
                        .build());

                log.info("Reserved {} units of SKU: {} for order: {}",
                        item.getQuantity(), item.getSku(), orderEvent.getOrderNumber());
            }

            // Publish success event
            InventoryEvent event = InventoryEvent.inventoryReserved(SERVICE_NAME, orderEvent.getOrderId(), reservationDataList);
            event.setOrderNumber(orderEvent.getOrderNumber());
            event.setCorrelationId(orderEvent.getCorrelationId());
            eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);

            log.info("Successfully reserved inventory for order: {}", orderEvent.getOrderNumber());

        } catch (Exception e) {
            log.error("Error reserving inventory for order: {}", orderEvent.getOrderNumber(), e);
            rollbackReservations(reservations);
            throw e;
        }
    }

    @Override
    @Transactional
    public void confirmReservation(String orderId) {
        log.info("Confirming reservation for order: {}", orderId);

        List<ReservedOrder> reservations = reservedOrderRepository
                .findByOrderIdAndStatus(orderId, ReservedOrder.ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            log.warn("No active reservations found for order: {}", orderId);
            return;
        }

        List<InventoryEvent.ReservationData> confirmedItems = new ArrayList<>();

        for (ReservedOrder reservation : reservations) {
            // Lock and update inventory
            Inventory inventory = inventoryRepository.findByProductIdWithLock(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            // Deduct actual quantity and decrease reserved
            inventory.setQuantity(inventory.getQuantity() - reservation.getReservedQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
            inventoryRepository.save(inventory);

            // Update reservation status
            reservation.setStatus(ReservedOrder.ReservationStatus.CONFIRMED);
            reservedOrderRepository.save(reservation);

            // Create transaction record
            Warehouse warehouse = warehouseRepository.findById(inventory.getWarehouseId()).orElse(null);
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .productId(reservation.getProductId())
                    .adjustedQuantity(-reservation.getReservedQuantity())
                    .note("Order confirmed: " + orderId)
                    .warehouse(warehouse)
                    .build();
            inventoryTransactionRepository.save(transaction);

            confirmedItems.add(InventoryEvent.ReservationData.builder()
                    .productId(reservation.getProductId())
                    .sku(reservation.getSku())
                    .quantity(reservation.getReservedQuantity().intValue())
                    .warehouseId(reservation.getWarehouseId())
                    .build());

            log.info("Confirmed reservation for order: {}, product: {}", orderId, reservation.getProductId());
        }

        // Publish confirmation event
        InventoryEvent event = new InventoryEvent(InventoryEvent.InventoryEventType.INVENTORY_CONFIRMED, SERVICE_NAME);
        event.setOrderNumber(orderId);
        event.setReservations(confirmedItems);
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    @Override
    @Transactional
    public void releaseReservations(String orderId) {
        log.info("Releasing reservations for order: {}", orderId);

        List<ReservedOrder> reservations = reservedOrderRepository
                .findByOrderIdAndStatus(orderId, ReservedOrder.ReservationStatus.RESERVED);

        if (reservations.isEmpty()) {
            log.info("No active reservations to release for order: {}", orderId);
            return;
        }

        List<InventoryEvent.ReservationData> releasedItems = new ArrayList<>();

        for (ReservedOrder reservation : reservations) {
            // Lock and update inventory - return stock to available
            Inventory inventory = inventoryRepository.findByProductIdWithLock(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
            inventoryRepository.save(inventory);

            // Update reservation status
            reservation.setStatus(ReservedOrder.ReservationStatus.CANCELLED);
            reservedOrderRepository.save(reservation);

            releasedItems.add(InventoryEvent.ReservationData.builder()
                    .productId(reservation.getProductId())
                    .sku(reservation.getSku())
                    .quantity(reservation.getReservedQuantity().intValue())
                    .warehouseId(reservation.getWarehouseId())
                    .build());

            log.info("Released reservation for order: {}, product: {}, quantity: {}",
                    orderId, reservation.getProductId(), reservation.getReservedQuantity());
        }

        // Publish release event (SAGA compensation)
        InventoryEvent event = InventoryEvent.inventoryReleased(SERVICE_NAME, null, releasedItems, "Order cancelled or payment failed");
        event.setOrderNumber(orderId);
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    @Override
    @Transactional
    public void processExpiredReservations() {
        log.info("Processing expired reservations");

        List<ReservedOrder> expiredReservations = reservedOrderRepository
                .findExpiredReservations(ReservedOrder.ReservationStatus.RESERVED, LocalDateTime.now());

        // Group by orderId and release
        expiredReservations.stream()
                .map(ReservedOrder::getOrderId)
                .distinct()
                .forEach(orderId -> {
                    log.info("Releasing expired reservations for order: {}", orderId);
                    releaseReservations(orderId);
                });

        log.info("Processed {} expired reservations", expiredReservations.size());
    }

    private void rollbackReservations(List<ReservedOrder> reservations) {
        for (ReservedOrder reservation : reservations) {
            try {
                Inventory inventory = inventoryRepository.findByProductId(reservation.getProductId()).orElse(null);
                if (inventory != null) {
                    inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getReservedQuantity());
                    inventoryRepository.save(inventory);
                }
                reservedOrderRepository.delete(reservation);
                log.debug("Rolled back reservation for product: {}", reservation.getProductId());
            } catch (Exception e) {
                log.error("Error rolling back reservation: {}", reservation.getId(), e);
            }
        }
    }

    private void publishInsufficientEvent(OrderEvent orderEvent, OrderEvent.OrderItemData item, long availableQuantity) {
        List<InventoryEvent.ReservationData> insufficientItems = List.of(
                InventoryEvent.ReservationData.builder()
                        .productId(item.getProductId())
                        .sku(item.getSku())
                        .quantity(item.getQuantity())
                        .availableQuantity(availableQuantity)
                        .build()
        );

        InventoryEvent event = InventoryEvent.inventoryInsufficient(SERVICE_NAME, orderEvent.getOrderId(), insufficientItems);
        event.setOrderNumber(orderEvent.getOrderNumber());
        event.setCorrelationId(orderEvent.getCorrelationId());
        event.setReason("Insufficient inventory for SKU: " + item.getSku());
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }
}
