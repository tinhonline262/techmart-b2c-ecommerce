package com.shopping.microservices.identity_service.event.listener;

import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.event.message.VerifyUserMailEvent;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.shopping.microservices.identity_service.enumeration.SignUpStatus.PENDING;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignUpEntityListener {

    private final KafkaTemplate<String, VerifyUserMailEvent> kafkaTemplate;

    @PostPersist
    @PostUpdate
    public void onSave(SignUpEntity signUpEntity) {
        if (signUpEntity.getStatus() != PENDING) {
            return;
        }
        log.info("Preparing to send verification email for user: {}", signUpEntity.getEmail());
        kafkaTemplate.send("user-verify", VerifyUserMailEvent.builder()
                .email(signUpEntity.getEmail())
                .name(signUpEntity.getName())
                .verifyToken(signUpEntity.getCurrentVerificationToken())
                .expiredDate(signUpEntity.getExpiredVerificationTokenDate())
                .build());
    }
}
