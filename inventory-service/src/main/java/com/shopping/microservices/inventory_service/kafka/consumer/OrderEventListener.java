package com.shopping.microservices.inventory_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.inventory_service.service.ReservationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class OrderEventListener {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;
    @KafkaListener(
            topics = KafkaTopics.ORDER_EVENTS,
            groupId = KafkaConsumerGroups.INVENTORY_SERVICE
    )
    public void handleOrderEvent(String message, Acknowledgment ack) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Received OrderEvent: type={}, orderId={}, orderNumber={}",
                    event.getEventType(), event.getOrderId(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "ORDER_CREATED" -> {
                    log.info("Processing ORDER_CREATED for order: {}", event.getOrderNumber());
                    reservationService.reserveInventory(event);
                }
                case "ORDER_CANCELLED" -> {
                    log.info("Processing ORDER_CANCELLED for order: {}", event.getOrderNumber());
                    reservationService.releaseReservations(event.getOrderId());
                }
                default -> log.debug("Ignoring event type: {}", event.getEventType());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing OrderEvent: {}", e.getMessage(), e);
        }
    }
}
