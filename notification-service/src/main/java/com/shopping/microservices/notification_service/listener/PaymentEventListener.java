package com.shopping.microservices.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.notification_service.service.MailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentEventListener {

    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.PAYMENT_EVENTS,
            groupId = KafkaConsumerGroups.INVENTORY_SERVICE
    )
    public void handlePaymentEvent(String message) {
        try {
            PaymentEvent event = objectMapper.readValue(message, PaymentEvent.class);
            log.info("Received PaymentEvent: type={}, orderId={}, orderNumber={}",
                    event.getEventType(), event.getOrderId(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "PAYMENT_SUCCESS" -> {
                    log.info("Processing PAYMENT_SUCCESS for order: {}", event.getOrderNumber());
                    mailService.sendPaymentSuccessMail(event);
                }
                default -> log.debug("Ignoring payment event type: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing PaymentEvent: {}", e.getMessage(), e);
        }
    }
}
