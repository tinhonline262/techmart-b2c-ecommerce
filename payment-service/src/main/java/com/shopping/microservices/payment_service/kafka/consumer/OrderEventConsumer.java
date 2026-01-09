package com.shopping.microservices.payment_service.kafka.consumer;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.event.InventoryReservedEvent;
import com.shopping.microservices.payment_service.event.OrderCreatedEvent;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {
    private final PaymentService paymentService;

    @KafkaListener(
            topics = "orders",
            groupId = "payment-service-order-created",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void consumeOrderCreatedEvent(OrderCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Received InventoryReservedEvent for orderId: {}, orderNumber: {}", event.orderId(), event.orderNumber());

        try {
            // Create payment for the order
            Payment payment = paymentService.createPayment(
                    event.orderId(),
                    event.orderNumber(),
                    event.totalAmount(),
                    PaymentMethod.valueOf(event.paymentMethod())
            );

            log.info("Payment created with id: {} for orderId: {}", payment.getId(), event.orderId());

            // Acknowledge the message after successful processing
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing InventoryReservedEvent for orderId: {}: {}", event.orderId(), e.getMessage());
            // Optionally, handle the failure (e.g., send to a dead-letter topic)
        }
    }
}
