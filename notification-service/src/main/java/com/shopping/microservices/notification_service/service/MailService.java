package com.shopping.microservices.notification_service.service;

import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;

public interface MailService {

    void sendVerifyUserMail(NotificationEvent verifyUserMailEvent);
    void sendCompleteUserMail(NotificationEvent completeUserMailEvent);
    void sendOrderPlacedMail(OrderEvent orderSendNotificationEvent);

    void sendOrderCompletedMail(OrderEvent event);

    void sendOrderCancelledMail(OrderEvent event);
    void sendPaymentSuccessMail(PaymentEvent paymentEvent);
}
