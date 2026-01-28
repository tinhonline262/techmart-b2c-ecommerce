package com.shopping.microservices.identity_service.event.listener;

import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.event.publisher.NotificationEventPublisher;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.shopping.microservices.identity_service.enumeration.SignUpStatus.PENDING;

@Component
@Slf4j
public class SignUpEntityListener {
    private final NotificationEventPublisher notificationEventPublisher;

    public SignUpEntityListener(NotificationEventPublisher notificationEventPublisher) {
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @PostPersist
    @PostUpdate
    public void onSave(SignUpEntity signUpEntity) {
        if (signUpEntity.getStatus() != PENDING) {
            return;
        }
        log.info("Preparing to send verification email for user: {}", signUpEntity.getEmail());

        notificationEventPublisher.publishEmailVerificationEvent(signUpEntity);
    }
}
