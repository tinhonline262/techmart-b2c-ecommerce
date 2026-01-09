package com.shopping.microservices.payment_service.kafka.consumer;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.event.InventoryReservedEvent;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "inventory_reserved",
            groupId = "payment-service-inventory-reserved",
            containerFactory = "inventoryReservedKafkaListenerContainerFactory"
    )
    public void consumeInventoryReservedEvent(InventoryReservedEvent event, Acknowledgment acknowledgment) {
        log.info("Received InventoryReservedEvent for orderId: {}, orderNumber: {}",
            event.orderId(), event.orderNumber());

        try {
            // Find the payment for this order
            Payment payment = paymentService.getPaymentByOrderId(event.orderId());

            log.info("Found payment id: {} for order: {}, payment method: {}",
                payment.getId(), event.orderNumber(), payment.getPaymentMethod());

            // Process the payment based on payment method
            paymentService.processPayment(payment.getId());

            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("InventoryReservedEvent acknowledged for orderId: {}", event.orderId());

        } catch (IllegalArgumentException e) {
            log.error("Payment not found for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing InventoryReservedEvent for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        }
    }
}

