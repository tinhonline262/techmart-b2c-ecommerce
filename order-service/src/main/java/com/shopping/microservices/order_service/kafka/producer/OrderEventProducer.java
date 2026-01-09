package com.shopping.microservices.order_service.kafka.producer;

import com.shopping.microservices.order_service.event.producer.OrderCancelledEvent;
import com.shopping.microservices.order_service.event.producer.OrderCompletedEvent;
import com.shopping.microservices.order_service.event.producer.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_ORDER_CREATED = "orders";
    private static final String TOPIC_ORDER_COMPLETED = "order-completed";

    private static final String TOPIC_ORDER_CANCELLED = "order-cancelled";

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishOrderCreatedEvent(OrderCreatedEvent event) {
        log.info("Gửi OrderCreatedEvent: {}", event);
        kafkaTemplate.send(TOPIC_ORDER_CREATED, String.valueOf(event.orderId()), event);
    }

    public void publishOrderCompletedEvent(OrderCompletedEvent event) {
        log.info("Gửi OrderCompletedEvent: {}", event);
        kafkaTemplate.send(TOPIC_ORDER_COMPLETED, String.valueOf(event.orderId()), event);
    }

    public void publishOrderCancelledEvent(OrderCancelledEvent event) {
        log.warn("Publishing OrderCancelledEvent for orderId: {}, orderNumber: {}, reason: {}",
                event.orderId(), event.orderNumber(), event.reason());
        kafkaTemplate.send(TOPIC_ORDER_CANCELLED, String.valueOf(event.orderId()), event);
    }


}
