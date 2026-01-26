package com.shopping.microservices.payment_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for inventory events.
 * 
 * Handles inventory-related events that affect payment processing:
 * - INVENTORY_INSUFFICIENT: May need to cancel pending payment
 * - INVENTORY_RELEASED: Inventory was released (order cancelled)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = KafkaTopics.INVENTORY_EVENTS,
        groupId = KafkaConsumerGroups.PAYMENT_SERVICE,
        containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void handleInventoryEvent(String message, Acknowledgment ack) {
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);
            log.info("Received InventoryEvent: type={}, orderId={}, orderNumber={}",
                event.getEventType(), event.getOrderId(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "INVENTORY_INSUFFICIENT" -> {
                    log.info("Processing INVENTORY_INSUFFICIENT for order: {}", event.getOrderNumber());
                    handleInventoryInsufficient(event);
                }
                case "INVENTORY_RELEASED" -> {
                    log.info("Processing INVENTORY_RELEASED for order: {}", event.getOrderNumber());
                    handleInventoryReleased(event);
                }
                default -> log.debug("Ignoring inventory event type: {}", event.getEventType());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing InventoryEvent: {}", e.getMessage(), e);
            // Don't acknowledge - message will be retried
        }
    }

    private void handleInventoryInsufficient(InventoryEvent event) {
        try {
            // Cancel pending payment if inventory is insufficient
            var paymentOpt = paymentService.getPaymentByOrderId(event.getOrderId());
            
            if (paymentOpt.isPresent()) {
                var payment = paymentOpt.get();
                if (!payment.getPaymentStatus().isTerminal()) {
                    log.info("Cancelling payment {} due to insufficient inventory for order: {}", 
                        payment.getId(), event.getOrderId());
                    paymentService.cancelPayment(payment.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error handling INVENTORY_INSUFFICIENT for order: {}", event.getOrderId(), e);
            throw e;
        }
    }

    private void handleInventoryReleased(InventoryEvent event) {
        // Inventory released typically means order was cancelled
        // Payment should already be cancelled via ORDER_CANCELLED event
        log.info("Inventory released for order: {}, payment should already be handled", event.getOrderId());
    }
}
