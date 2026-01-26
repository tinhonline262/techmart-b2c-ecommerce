package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.inventory_service.dto.ReservationContext;
import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import com.shopping.microservices.inventory_service.exception.InsufficientInventoryException;
import com.shopping.microservices.inventory_service.exception.InventoryReservationException;
import com.shopping.microservices.inventory_service.exception.ProductNotFoundException;
import com.shopping.microservices.inventory_service.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final InventoryService inventoryService;
    private final ReservedOrderService reservedOrderService;
    private final WarehouseService warehouseService;
    private final InventoryTransactionService inventoryTransactionService;
    private final EventPublisher eventPublisher;

    private static final String SERVICE_NAME = "inventory-service";
    private static final int RESERVATION_EXPIRY_MINUTES = 30;

    @Override
    @Transactional
    public void reserveInventory(OrderEvent orderEvent) {
        log.info("Processing inventory reservation for order: {}", orderEvent.getOrderNumber());

        try {
            ReservationContext context = performReservation(orderEvent);
            publishSuccessEvent(orderEvent, context);
            log.info("Successfully reserved inventory for order: {}", orderEvent.getOrderNumber());

        } catch (InsufficientInventoryException e) {
            log.warn("Insufficient inventory for order: {}", orderEvent.getOrderNumber(), e);
            publishInsufficientEvent(orderEvent, e);

        } catch (Exception e) {
            log.error("Error reserving inventory for order: {}", orderEvent.getOrderNumber(), e);
            throw new InventoryReservationException("Failed to reserve inventory", e);
        }
    }

    @Transactional
    protected ReservationContext performReservation(OrderEvent orderEvent) {
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(RESERVATION_EXPIRY_MINUTES);

        // Batch fetch inventories
        List<String> skus = orderEvent.getItems().stream()
                .map(OrderEvent.OrderItemData::getSku)
                .toList();

        Map<String, Inventory> inventoryMap = inventoryService.findAndLockBySkus(skus);
        List<ReservationContext.ReservationResult> results = new ArrayList<>();

        for (OrderEvent.OrderItemData item : orderEvent.getItems()) {
            Inventory inventory = inventoryMap.get(item.getSku());

            if (inventory == null) {
                throw new ProductNotFoundException("Product not found: " + item.getSku());
            }

            long availableQuantity = inventoryService.getAvailableQuantity(inventory);

            if (availableQuantity < item.getQuantity()) {
                throw new InsufficientInventoryException(
                        item.getSku(),
                        item.getProductId(),
                        item.getQuantity(),
                        availableQuantity
                );
            }

            // Reserve inventory
            inventoryService.incrementReservedQuantity(inventory, item.getQuantity());

            // Create reservation
            ReservedOrder reservation = reservedOrderService.createReservation(
                    orderEvent.getOrderId(),
                    item,
                    inventory.getWarehouseId(),
                    expiresAt
            );
            results.add(ReservationContext.ReservationResult.builder()
                            .reservation(reservation)
                            .inventory(inventory)
                            .requestedQuantity(item.getQuantity())
                            .availableAfterReservation(availableQuantity - item.getQuantity())
                    .build());

            log.info("Reserved {} units of SKU: {} for order: {}",
                    item.getQuantity(), item.getSku(), orderEvent.getOrderNumber());
        }

        return new ReservationContext(results);
    }

    @Override
    @Transactional
    public void confirmReservation(PaymentEvent paymentEvent) {
        log.info("Confirming reservation for order: {}", paymentEvent.getOrderId());

        List<ReservedOrder> reservations = reservedOrderService
                .findActiveReservations(paymentEvent.getOrderId());

        if (reservations.isEmpty()) {
            log.warn("No active reservations found for order: {}", paymentEvent.getOrderId());
            return;
        }

        List<InventoryEvent.ReservationData> confirmedItems = new ArrayList<>();

        for (ReservedOrder reservation : reservations) {
            Inventory inventory = inventoryService.findAndLockByProductId(reservation.getProductId());

            // Deduct actual quantity and decrease reserved
            inventoryService.confirmReservation(inventory, reservation.getReservedQuantity());

            // Update reservation status
            reservedOrderService.confirmReservation(reservation);

            // Create transaction record
            inventoryTransactionService.createInventoryTransaction(
                    inventory,
                    -reservation.getReservedQuantity(),
                    "Order confirmed: " + paymentEvent.getOrderNumber()
            );

            confirmedItems.add(buildReservationData(reservation));

            log.info("Confirmed reservation for order: {}, product: {}",
                    paymentEvent.getOrderId(), reservation.getProductId());
        }

        // Publish confirmation event
        InventoryEvent event = InventoryEvent.inventoryConfirmed(
                SERVICE_NAME,
                paymentEvent.getOrderId(),
                paymentEvent.getOrderNumber(),
                confirmedItems
        );
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    @Override
    @Transactional
    public void releaseReservations(Long orderId) {
        log.info("Releasing reservations for order: {}", orderId);

        List<ReservedOrder> reservations = reservedOrderService.findActiveReservations(orderId);

        if (reservations.isEmpty()) {
            log.info("No active reservations to release for order: {}", orderId);
            return;
        }

        List<InventoryEvent.ReservationData> releasedItems = new ArrayList<>();

        for (ReservedOrder reservation : reservations) {
            Inventory inventory = inventoryService.findAndLockByProductId(reservation.getProductId());

            // Release reserved quantity
            inventoryService.decrementReservedQuantity(inventory, reservation.getReservedQuantity());

            // Update reservation status
            reservedOrderService.cancelReservation(reservation);

            releasedItems.add(buildReservationData(reservation));

            log.info("Released reservation for order: {}, product: {}, quantity: {}",
                    orderId, reservation.getProductId(), reservation.getReservedQuantity());
        }

        // Publish release event
        InventoryEvent event = InventoryEvent.inventoryReleased(
                SERVICE_NAME,
                orderId,
                releasedItems,
                "Order cancelled or payment failed"
        );
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    @Override
    @Transactional
    public void processExpiredReservations() {
        log.info("Processing expired reservations");

        List<Long> expiredOrderIds = reservedOrderService.findExpiredOrderIds(LocalDateTime.now());

        expiredOrderIds.forEach(orderId -> {
            log.info("Releasing expired reservations for order: {}", orderId);
            releaseReservations(orderId);
        });

        log.info("Processed {} expired orders", expiredOrderIds.size());
    }

    // ========== Helper Methods ==========

    private void publishSuccessEvent(OrderEvent orderEvent, ReservationContext context) {
        // Batch fetch warehouse names
        Set<Long> warehouseIds = context.getResults().stream()
                .map(r -> r.getInventory().getWarehouseId())
                .collect(Collectors.toSet());

        Map<Long, String> warehouseNames = warehouseService.getWarehouseNamesByIds(warehouseIds);

        List<InventoryEvent.ReservationData> reservationDataList = context.getResults().stream()
                .map(result -> buildReservationData(result, warehouseNames))
                .toList();

        InventoryEvent event = InventoryEvent.inventoryReserved(
                SERVICE_NAME,
                orderEvent.getOrderId(),
                orderEvent.getOrderNumber(),
                reservationDataList
        );
        event.setCorrelationId(orderEvent.getCorrelationId());
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    private void publishInsufficientEvent(OrderEvent orderEvent, InsufficientInventoryException e) {
        List<InventoryEvent.ReservationData> insufficientItems = List.of(
                InventoryEvent.ReservationData.builder()
                        .productId(e.getProductId())
                        .sku(e.getSku())
                        .quantity(e.getRequested())
                        .availableQuantity(e.getAvailable())
                        .build()
        );

        InventoryEvent event = InventoryEvent.inventoryInsufficient(
                SERVICE_NAME,
                orderEvent.getOrderId(),
                orderEvent.getOrderNumber(),
                insufficientItems
        );
        event.setCorrelationId(orderEvent.getCorrelationId());
        event.setReason("Insufficient inventory for SKU: " + e.getSku());
        eventPublisher.publish(KafkaTopics.INVENTORY_EVENTS, event);
    }

    private InventoryEvent.ReservationData buildReservationData(
           ReservationContext.ReservationResult result,
            Map<Long, String> warehouseNames) {

        ReservedOrder reservation = result.getReservation();
        Inventory inventory = result.getInventory();

        return InventoryEvent.ReservationData.builder()
                .productId(reservation.getProductId())
                .sku(reservation.getSku())
                .quantity(result.getRequestedQuantity())
                .warehouseId(inventory.getWarehouseId())
                .warehouseName(warehouseNames.getOrDefault(
                        inventory.getWarehouseId(), "Unknown"))
                .availableQuantity(result.getAvailableAfterReservation())
                .build();
    }

    private InventoryEvent.ReservationData buildReservationData(ReservedOrder reservation) {
        return InventoryEvent.ReservationData.builder()
                .productId(reservation.getProductId())
                .sku(reservation.getSku())
                .quantity(reservation.getReservedQuantity().intValue())
                .warehouseId(reservation.getWarehouseId())
                .build();
    }
}