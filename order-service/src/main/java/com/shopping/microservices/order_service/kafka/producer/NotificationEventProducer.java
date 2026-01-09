package com.shopping.microservices.order_service.kafka.producer;

import com.shopping.microservices.order_service.event.producer.OrderSendNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class NotificationEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_ORDER_CREATED = "order-created";

    public void publishOrderCreatedNotificationEvent(OrderSendNotificationEvent event) {
        log.info("Gửi OrderCreatedEvent: {}", event);
        kafkaTemplate.send(TOPIC_ORDER_CREATED, event);
    }
}
