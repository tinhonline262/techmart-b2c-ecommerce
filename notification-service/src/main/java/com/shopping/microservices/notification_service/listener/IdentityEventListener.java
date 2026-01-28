package com.shopping.microservices.notification_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.notification_service.service.MailService;
import jakarta.inject.Qualifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityEventListener {
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = KafkaTopics.EMAIL_NOTIFICATIONS
        ,groupId = KafkaConsumerGroups.EMAIL_NOTIFICATION_GROUP)
    public void handleEmailEvent(String message) {
        try {
            NotificationEvent event = objectMapper.readValue(message, NotificationEvent.class);
            log.info("Received email notification event: {}", message);

            switch (event.getTemplate()) {
                case ACCOUNT_VERIFICATION -> {
                    log.info("Processing ACCOUNT_VERIFICATION email for: {}", event.getData().get("email"));
                    mailService.sendVerifyUserMail(event);
                }
                case WELCOME -> {
                    log.info("Processing WELCOME email for: {}", event.getData().get("email"));
                    mailService.sendCompleteUserMail(event);
                }
                default -> log.debug("Ignoring email template: {}", event.getTemplate());
            }
        } catch (Exception e) {
            log.error("Error processing email notification event: {}", e.getMessage(), e);
        }
    }
}
