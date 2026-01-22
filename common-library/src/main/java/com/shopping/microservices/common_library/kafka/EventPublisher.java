package com.shopping.microservices.common_library.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                          @Qualifier("restObjectMapper") ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<String, String>> publish(String topic, BaseEvent event) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            String key = event.getEventId();
            
            log.debug("Publishing event to topic '{}' with key '{}': eventType={}, correlationId={}", 
                topic, key, event.getEventType(), event.getCorrelationId());
            
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, eventJson);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully published event to topic '{}': eventId={}, eventType={}, partition={}, offset={}", 
                        topic, 
                        event.getEventId(), 
                        event.getEventType(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event to topic '{}': eventId={}, eventType={}, error={}", 
                        topic, 
                        event.getEventId(), 
                        event.getEventType(), 
                        ex.getMessage(), 
                        ex);
                }
            });
            
            return future;
            
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event to JSON: eventId={}, eventType={}, error={}", 
                event.getEventId(), 
                event.getEventType(), 
                e.getMessage(), 
                e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<SendResult<String, String>> publishSync(String topic, BaseEvent event) {
        CompletableFuture<SendResult<String, String>> future = publish(topic, event);
        try {
            future.get();
            return future;
        } catch (Exception e) {
            log.error("Error during synchronous publish: eventId={}, topic={}, error={}", 
                event.getEventId(), topic, e.getMessage(), e);
            throw new RuntimeException("Failed to publish event synchronously", e);
        }
    }
}
