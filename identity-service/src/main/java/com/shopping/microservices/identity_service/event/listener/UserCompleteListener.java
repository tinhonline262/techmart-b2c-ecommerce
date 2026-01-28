package com.shopping.microservices.identity_service.event.listener;

import com.shopping.microservices.identity_service.entity.UserEntity;
import com.shopping.microservices.identity_service.event.publisher.NotificationEventPublisher;
import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCompleteListener {

    private final NotificationEventPublisher notificationEventPublisher;

    public UserCompleteListener(NotificationEventPublisher notificationEventPublisher) {
        this.notificationEventPublisher = notificationEventPublisher;
    }

    @PostPersist
    public void onSave(UserEntity entity) {
        log.info("Preparing to send completion email for user: {}", entity.getEmail());

        notificationEventPublisher.publishWelcomeEmailEvent(entity);
    }
}
