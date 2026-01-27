package com.shopping.microservices.inventory_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.inventory_service.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = KafkaConsumerGroups.INVENTORY_SERVICE
    )
    public void handlePaymentEvent(String message, Acknowledgment ack) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            log.info("Received PaymentEvent: type={}, orderId={}, orderNumber={}",
                    event.getEventType(), event.getOrderId(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "PAYMENT_SUCCESS" -> {
                    log.info("Processing PAYMENT_SUCCESS for order: {}", event.getOrderNumber());
                    reservationService.confirmReservation(event);
                }
                case "PAYMENT_FAILED" -> {
                    log.info("Processing PAYMENT_FAILED for order: {}", event.getOrderNumber());
                    reservationService.releaseReservations(event.getOrderId());
                }
                default -> log.debug("Ignoring payment event type: {}", event.getEventType());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PaymentEvent: {}", e.getMessage(), e);
        }
    }
}
