package com.shopping.microservices.product_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final ObjectMapper objectMapper;
    private final ProductService productService;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_EVENTS,
            groupId = KafkaConsumerGroups.INVENTORY_SERVICE
    )
    public void handleInventoryEvent(String message) {
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);
            log.info("Received inventory event: {} for order: {}", event.getEventType(), event.getOrderId());

            InventoryEvent.InventoryEventType eventType = InventoryEvent.InventoryEventType.valueOf(event.getEventType());

            switch (eventType) {
                case INVENTORY_CONFIRMED -> {
                    productService.updateProductQuantity(event);
                    log.info("Updated product quantities for confirmed inventory event: {}", event.getOrderNumber());
                }
                default -> log.debug("Ignoring inventory event type: {}", event.getEventType());
            }

        } catch (Exception e) {
            log.error("Error processing inventory event: {}", e.getMessage(), e);
        }
    }
}
