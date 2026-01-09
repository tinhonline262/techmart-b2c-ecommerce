package com.shopping.microservices.inventory_service.kafka.consumer;

import com.shopping.microservices.inventory_service.event.*;
import com.shopping.microservices.inventory_service.kafka.producer.InventoryEventProducer;
import com.shopping.microservices.inventory_service.service.InventoryReservationService;
import com.shopping.microservices.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryEventProducer inventoryEventProducer;
    private final InventoryReservationService inventoryReservationService;

    @KafkaListener(
            topics = "orders",
            groupId = "inventory-service-order-created",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Received OrderCreatedEvent for order: {}", event.orderNumber());

        try {
            // Process reservation for the order
            boolean success = inventoryReservationService.processOrderReservation(event);

            if (success) {
                // Publish success event
                InventoryReservedEvent reservedEvent = inventoryReservationService
                        .buildReservedEvent(event);
                inventoryEventProducer.publishInventoryReservedEvent(reservedEvent);
//                kafkaTemplate.send(INVENTORY_RESERVED_TOPIC, event.orderNumber(), reservedEvent);
                log.info("Published InventoryReservedEvent for order: {}", event.orderNumber());
            } else {
                // Publish failure event
                InventoryFailedEvent failedEvent = InventoryFailedEvent.builder()
                        .orderId(event.orderId())
                        .orderNumber(event.orderNumber())
                        .reason("Insufficient inventory")
                        .build();
                inventoryEventProducer.publishInventoryFailedEvent(failedEvent);
//                kafkaTemplate.send(INVENTORY_FAILED_TOPIC, event.orderNumber(), failedEvent);
                log.warn("Published InventoryFailedEvent for order: {}", event.orderNumber());
            }
        } catch (Exception e) {
            log.error("Error processing OrderCreatedEvent for order: {}", event.orderNumber(), e);

            // Publish failure event
            InventoryFailedEvent failedEvent = InventoryFailedEvent.builder()
                    .orderId(event.orderId())
                    .orderNumber(event.orderNumber())
                    .reason("Error processing reservation: " + e.getMessage())
                    .build();
            inventoryEventProducer.publishInventoryFailedEvent(failedEvent);
//            kafkaTemplate.send(INVENTORY_FAILED_TOPIC, event.orderNumber(), failedEvent);
        }
    }

    /**
     * Listen for OrderConfirmedEvent to confirm reservations
     */
    @KafkaListener(
            topics = "order-completed",
            groupId = "inventory-service-order-completed",
            containerFactory = "orderCompletedKafkaListenerContainerFactory"
    )
    public void handleOrderConfirmed(OrderCompletedEvent event) {
        log.info("Received OrderConfirmedEvent for order: {}", event.orderNumber());

        try {
            inventoryReservationService.confirmReservation(event.orderNumber());
            log.info("Successfully confirmed reservation for order: {}", event.orderNumber());
        } catch (Exception e) {
            log.error("Error confirming reservation for order: {}", event.orderNumber(), e);
        }
    }

    /**
     * Listen for OrderCancelledEvent to cancel reservations
     */
    @KafkaListener(
            topics = "order-cancelled",
            groupId = "inventory-service-order-cancelled",
            containerFactory = "orderCancelledKafkaListenerContainerFactory"
    )
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("Received OrderCancelledEvent for order: {}", event.orderNumber());

        try {
            inventoryReservationService.cancelReservation(event.orderNumber());
            log.info("Successfully cancelled reservation for order: {}", event.orderNumber());
        } catch (Exception e) {
            log.error("Error cancelling reservation for order: {}", event.orderNumber(), e);
        }
    }
}

