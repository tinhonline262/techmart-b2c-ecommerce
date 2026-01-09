package com.shopping.microservices.order_service.kafka.consumer;

import com.shopping.microservices.order_service.event.consumer.PaymentCompletedEvent;
import com.shopping.microservices.order_service.event.consumer.PaymentFailedEvent;
import com.shopping.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final OrderService orderService;

    /**
     * Consume PaymentCompletedEvent
     * When payment is completed successfully, update order status to COMPLETED
     * and publish OrderCompletedEvent
     */
    @KafkaListener(
            topics = "payment_completed",
            groupId = "order-service-payment-completed",
            containerFactory = "paymentCompletedKafkaListenerContainerFactory"
    )
    public void consumePaymentCompletedEvent(PaymentCompletedEvent event, Acknowledgment acknowledgment) {
        log.info("Received PaymentCompletedEvent for orderId: {}, orderNumber: {}",
            event.orderId(), event.orderNumber());

        try {
            // Complete the order
            orderService.completeOrder(
                event.orderId(),
                "Payment completed successfully. Payment ID: " + event.paymentId() +
                ", Method: " + event.paymentMethod()
            );

            log.info("Order {} completed successfully after payment", event.orderNumber());

            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("PaymentCompletedEvent acknowledged for orderId: {}", event.orderId());

        } catch (IllegalArgumentException e) {
            log.error("Order not found for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        }
    }

    /**
     * Consume PaymentFailedEvent
     * When payment fails, update order status to CANCELLED
     * and publish OrderCancelledEvent
     */
    @KafkaListener(
            topics = "payment_failed",
            groupId = "order-service-payment-failed",
            containerFactory = "paymentFailedKafkaListenerContainerFactory"
    )
    public void consumePaymentFailedEvent(PaymentFailedEvent event, Acknowledgment acknowledgment) {
        log.warn("Received PaymentFailedEvent for orderId: {}, orderNumber: {}, reason: {}",
            event.orderId(), event.orderNumber(), event.reason());

        try {
            // Cancel the order
            orderService.cancelOrder(
                event.orderId(),
                "Payment failed: " + event.reason() +
                ". Payment ID: " + event.paymentId() +
                ", Method: " + event.paymentMethod()
            );

            log.info("Order {} cancelled due to payment failure", event.orderNumber());

            // Acknowledge the message
            acknowledgment.acknowledge();
            log.info("PaymentFailedEvent acknowledged for orderId: {}", event.orderId());

        } catch (IllegalArgumentException e) {
            log.error("Order not found for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PaymentFailedEvent for orderId: {}", event.orderId(), e);
            // Acknowledge to avoid infinite retry
            acknowledgment.acknowledge();
        }
    }
}

