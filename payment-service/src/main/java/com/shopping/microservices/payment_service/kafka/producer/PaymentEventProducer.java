package com.shopping.microservices.payment_service.kafka.producer;

import com.shopping.microservices.payment_service.event.PaymentCompletedEvent;
import com.shopping.microservices.payment_service.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PAYMENT_COMPLETED = "payment_completed";
    private static final String TOPIC_PAYMENT_FAILED = "payment_failed";

    public void publishPaymentCompletedEvent(PaymentCompletedEvent event) {
        log.info("Publishing PaymentCompletedEvent for orderId: {}, orderNumber: {}",
            event.orderId(), event.orderNumber());
        kafkaTemplate.send(TOPIC_PAYMENT_COMPLETED, String.valueOf(event.orderId()), event);
    }

    public void publishPaymentFailedEvent(PaymentFailedEvent event) {
        log.warn("Publishing PaymentFailedEvent for orderId: {}, orderNumber: {}, reason: {}",
            event.orderId(), event.orderNumber(), event.reason());
        kafkaTemplate.send(TOPIC_PAYMENT_FAILED, String.valueOf(event.orderId()), event);
    }
}

