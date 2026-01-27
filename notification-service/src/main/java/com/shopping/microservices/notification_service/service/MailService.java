package com.shopping.microservices.notification_service.service;

import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.notification_service.event.*;

public interface MailService {

    void sendVerifyUserMail(VerifyUserMailEvent verifyUserMailEvent);
    void sendCompleteUserMail(CompleteUserMailEvent completeUserMailEvent);
    void sendOrderPlacedMail(OrderEvent orderSendNotificationEvent);

    void sendOrderCompletedMail(OrderEvent event);

    void sendOrderCancelledMail(OrderEvent event);
}
