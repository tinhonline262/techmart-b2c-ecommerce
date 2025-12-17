package com.shopping.microservices.notification_service.service;

import com.shopping.microservices.notification_service.event.CompleteUserMailEvent;
import com.shopping.microservices.notification_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.notification_service.event.VerifyUserMailEvent;

public interface MailService {

    void sendVerifyUserMail(VerifyUserMailEvent verifyUserMailEvent);
    void sendCompleteUserMail(CompleteUserMailEvent completeUserMailEvent);
    void sendOrderPlacedMail(OrderSendNotificationEvent orderSendNotificationEvent);
}
