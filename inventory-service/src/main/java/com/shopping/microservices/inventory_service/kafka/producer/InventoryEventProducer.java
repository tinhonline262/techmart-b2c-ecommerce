package com.shopping.microservices.inventory_service.kafka.producer;

import com.shopping.microservices.inventory_service.event.InventoryFailedEvent;
import com.shopping.microservices.inventory_service.event.InventoryReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_INVENTORY_RESERVED = "inventory_reserved";
    private static final String TOPIC_INVENTORY_FAILED = "inventory_failed";

    public void publishInventoryReservedEvent(InventoryReservedEvent event) {
        log.info("Publishing InventoryReservedEvent for orderId: {}", event.orderId());
        kafkaTemplate.send(TOPIC_INVENTORY_RESERVED, String.valueOf(event.orderId()), event);
    }

    public void publishInventoryFailedEvent(InventoryFailedEvent event) {
        log.warn("Publishing InventoryFailedEvent for orderId: {}, reason: {}", event.orderId(), event.reason());
        kafkaTemplate.send(TOPIC_INVENTORY_FAILED, String.valueOf(event.orderId()), event);
    }
}

