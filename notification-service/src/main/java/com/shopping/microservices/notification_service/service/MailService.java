package com.shopping.microservices.notification_service.service;

import com.shopping.microservices.notification_service.event.*;

public interface MailService {

    void sendVerifyUserMail(VerifyUserMailEvent verifyUserMailEvent);
    void sendCompleteUserMail(CompleteUserMailEvent completeUserMailEvent);
    void sendOrderPlacedMail(OrderSendNotificationEvent orderSendNotificationEvent);

    void sendOrderCompletedMail(OrderCompletedEvent event);

    void sendOrderCancelledMail(OrderCancelledEvent event);
}
