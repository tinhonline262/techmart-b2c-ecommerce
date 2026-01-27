package com.shopping.microservices.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.notification_service.event.OrderCancelledEvent;
import com.shopping.microservices.notification_service.event.OrderCompletedEvent;
import com.shopping.microservices.notification_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.notification_service.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.ORDER_EVENTS,
            groupId = KafkaConsumerGroups.ORDER_SERVICE)
    public void handleOrderEvent(String message, Acknowledgment ack) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Received OrderEvent: type={}, orderId={}, orderNumber={}",
                    event.getEventType(), event.getOrderId(), event.getOrderNumber());
            switch (event.getEventType()) {
                case "ORDER_CREATED" -> {
                    log.info("Processing ORDER_CREATED for order: {}", event.getOrderNumber());

                    mailService.sendOrderPlacedMail(event);
                }
                case "ORDER_COMPLETED" -> {
                    log.info("Processing ORDER_COMPLETED for order: {}", event.getOrderNumber());

                    mailService.sendOrderCompletedMail(event);
                }
                case "ORDER_CANCELLED" -> {
                    log.info("Processing ORDER_CANCELLED for order: {}", event.getOrderNumber());

                    mailService.sendOrderCancelledMail(event);
                }
                default -> log.debug("Ignoring event type: {}", event.getEventType());
            }
            log.info("Successfully processed OrderEvent for orderId={}", event.getOrderId());

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing PaymentEvent: {}", e.getMessage(), e);
        }
    }

}
