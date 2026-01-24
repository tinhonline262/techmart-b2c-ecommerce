package com.shopping.microservices.order_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = KafkaConsumerGroups.PAYMENT_SERVICE
    )
    public void handlePaymentEvent(String message, Acknowledgment acknowledgment) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            log.info("Received payment event: {} for order: {}", event.getEventType(), event.getOrderId());

            PaymentEvent.PaymentEventType eventType = PaymentEvent.PaymentEventType.valueOf(event.getEventType());

            switch (eventType) {
                case PAYMENT_SUCCESS -> {
                    orderService.confirmOrder(event.getOrderId());
                    log.info("Order {} confirmed after payment success", event.getOrderId());
                }
                case PAYMENT_FAILED -> {
                    orderService.cancelOrder(event.getOrderId(), "Payment failed: " + event.getFailureReason());
                    log.warn("Order {} cancelled due to payment failure: {}", event.getOrderId(), event.getFailureReason());
                }
                default -> log.debug("Ignoring payment event type: {}", event.getEventType());
            }

            acknowledgment.acknowledge();

        } catch (IllegalArgumentException e) {
            log.error("Order not found for orderId: {}", e.getMessage());
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }
}
