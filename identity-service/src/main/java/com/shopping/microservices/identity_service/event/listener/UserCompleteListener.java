package com.shopping.microservices.identity_service.event.listener;

import com.shopping.microservices.identity_service.event.message.CompleteUserMailEvent;
import com.shopping.microservices.identity_service.entity.UserEntity;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCompleteListener {

    private final KafkaTemplate<String, CompleteUserMailEvent> kafkaTemplate;

    @PostPersist
    public void onSave(UserEntity entity) {
        log.info("Preparing to send completion email for user: {}", entity.getEmail());
        kafkaTemplate.send("user-completed", CompleteUserMailEvent.builder()
                .email(entity.getEmail())
                .name(entity.getName())
                .username(entity.getUsername())
                .createdAt(entity.getCreatedAt())
                .build());
    }
}
