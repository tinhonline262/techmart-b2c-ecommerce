package com.shopping.microservices.order_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.order_service.enumeration.OrderProgress;
import com.shopping.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryEventListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = KafkaTopics.INVENTORY_EVENTS,
            groupId = KafkaConsumerGroups.INVENTORY_SERVICE
    )
    public void handleInventoryEvent(String message, Acknowledgment acknowledgment) {
        try {
            InventoryEvent event = objectMapper.readValue(message, InventoryEvent.class);
            log.info("Received inventory event: {} for order: {}", event.getEventType(), event.getOrderId());

            InventoryEvent.InventoryEventType eventType = InventoryEvent.InventoryEventType.valueOf(event.getEventType());

            switch (eventType) {
                case INVENTORY_RESERVED -> {
                    orderService.updateOrderProgress(event.getOrderId(), OrderProgress.INVENTORY_RESERVED, null);
                    log.info("Order {} inventory reserved successfully", event.getOrderId());
                }
                case INVENTORY_INSUFFICIENT -> {
                    orderService.cancelOrder(event.getOrderId(), "Insufficient inventory: " + event.getReason());
                    log.warn("Order {} cancelled due to insufficient inventory", event.getOrderId());
                }
                case INVENTORY_CONFIRMED -> {
                    orderService.updateOrderProgress(event.getOrderId(), OrderProgress.READY_TO_SHIP, null);
                    log.info("Order {} inventory confirmed, ready to ship", event.getOrderId());
                }
                default -> log.debug("Ignoring inventory event type: {}", event.getEventType());
            }

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("Error processing inventory event: {}", e.getMessage(), e);
            acknowledgment.acknowledge();
        }
    }
}
