package com.shopping.microservices.identity_service.event.publisher;

import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Publisher for payment-related events.
 * 
 * Encapsulates event creation and publishing logic for payment domain events.
 * Uses the common EventPublisher for actual Kafka operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventPublisher {

    private final EventPublisher eventPublisher;
    private static final String SOURCE = "identity-service";

    public void publishEmailVerificationEvent(SignUpEntity signUpEntity) {
        NotificationEvent event = NotificationEvent.builder()
                .recipient(signUpEntity.getEmail())
                .template(NotificationEvent.NotificationTemplate.ACCOUNT_VERIFICATION)
                .subject("Verify your account")
                .customerName(signUpEntity.getName())
                .priority(NotificationEvent.NotificationPriority.HIGH)
                .data(Map.of(
                        "verifyToken", signUpEntity.getCurrentVerificationToken(),
                        "email", signUpEntity.getEmail(),
                        "name", signUpEntity.getName(),
                        "expiredDate", signUpEntity.getExpiredVerificationTokenDate()
                ))
                .build();
        event.setEventType(NotificationEvent.NotificationEventType.EMAIL_SEND.name());
        event.setSource(SOURCE);
        event.setCorrelationId(signUpEntity.getId());

        log.info("Publishing EMAIL_SEND event for user: {}", signUpEntity.getEmail());
        eventPublisher.publish(KafkaTopics.EMAIL_NOTIFICATIONS, event);
    }

    public void publishWelcomeEmailEvent(UserEntity signUpEntity) {
        NotificationEvent event = NotificationEvent.builder()
                .recipient(signUpEntity.getEmail())
                .template(NotificationEvent.NotificationTemplate.WELCOME)
                .subject("Welcome to Our Service!")
                .customerName(signUpEntity.getName())
                .priority(NotificationEvent.NotificationPriority.HIGH)
                .data(Map.of(
                        "email", signUpEntity.getEmail(),
                        "name", signUpEntity.getName(),
                        "username", signUpEntity.getUsername(),
                        "createdAt", signUpEntity.getCreatedAt()
                ))
                .build();
        event.setEventType(NotificationEvent.NotificationEventType.EMAIL_SEND.name());
        event.setSource(SOURCE);
        event.setCorrelationId(signUpEntity.getId());

        log.info("Publishing WELCOME email event for user: {}", signUpEntity.getEmail());
        eventPublisher.publish(KafkaTopics.EMAIL_NOTIFICATIONS, event);
    }
}
