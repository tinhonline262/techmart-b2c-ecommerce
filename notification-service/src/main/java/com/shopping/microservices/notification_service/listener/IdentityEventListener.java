package com.shopping.microservices.notification_service.listener;

import com.shopping.microservices.notification_service.event.CompleteUserMailEvent;
import com.shopping.microservices.notification_service.event.VerifyUserMailEvent;
import com.shopping.microservices.notification_service.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class IdentityEventListener {
    private final MailService mailService;

    @KafkaListener(topics = "user-verify",
            groupId = "notification-service-verify-mail-group",
            containerFactory = "verifyUserMailEventListenerFactory")
    public void handleVerifyEvent(VerifyUserMailEvent event) {
        log.info("Sending verification email to user: {}", event.name());
        mailService.sendVerifyUserMail(event);
        log.info("Verification email sent to: {}", event.email());
    }
    @KafkaListener(topics = "user-completed",
            groupId = "notification-service-completed-user-group",
            containerFactory = "completeUserMailEventListenerFactory")
    public void handleCompleteEvent(CompleteUserMailEvent event) {
        log.info("Sending completion email to user: {}", event.name());
        mailService.sendCompleteUserMail(event);
        log.info("Completion email sent to: {}", event.email());
    }
}
