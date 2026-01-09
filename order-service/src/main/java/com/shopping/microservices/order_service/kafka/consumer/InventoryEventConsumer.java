package com.shopping.microservices.order_service.kafka.consumer;

import com.shopping.microservices.order_service.event.consumer.InventoryFailedEvent;
import com.shopping.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final OrderService orderService;

    /**
     * Consume InventoryFailedEvent
     * When inventory reservation fails, update order status to CANCELLED
     * and publish OrderCancelledEvent
     */
    @KafkaListener(
            topics = "inventory_failed",
            groupId = "order-service-inventory-failed",
            containerFactory = "inventoryFailedKafkaListenerContainerFactory"
    )
    public void consumeInventoryFailedEvent(InventoryFailedEvent event, Acknowledgment acknowledgment) {
        log.warn("Received InventoryFailedEvent for orderId: {}, orderNumber: {}, reason: {}",
            event.orderId(), event.orderNumber(), event.reason());

        try {
            // Cancel the order due to inventory failure
            String reason = "Inventory reservation failed: " + event.reason();
            if (event.failedSku() != null) {
                reason += " (SKU: " + event.failedSku() + ")";
            }

            orderService.cancelOrder(event.orderId(), reason);

            log.info("Order {} cancelled due to inventory failure", event.orderNumber());

            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("InventoryFailedEvent acknowledged for orderId: {}", event.orderId());

        } catch (IllegalArgumentException e) {
            log.error("Order not found for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing InventoryFailedEvent for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        }
    }
}
