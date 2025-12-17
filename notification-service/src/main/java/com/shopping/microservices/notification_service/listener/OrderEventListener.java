package com.shopping.microservices.notification_service.listener;

import com.shopping.microservices.notification_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.notification_service.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderEventListener {
    private final MailService mailService;
    @KafkaListener(topics = "order-created", groupId = "notification-group", containerFactory = "orderCreatedEventListenerFactory")
    public void handleOrderEvent(OrderSendNotificationEvent event) {
        log.info("Sending notification for order: {} to customer: {}", event.orderNumber(), event.customerName());
        mailService.sendOrderPlacedMail(event);
        log.info("Notification send for: {}", event.orderNumber());
    }
}
