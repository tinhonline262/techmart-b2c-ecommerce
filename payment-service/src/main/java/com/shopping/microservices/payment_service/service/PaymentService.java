package com.shopping.microservices.payment_service.service;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentService {

    Payment createPayment(Long orderId, String orderNumber, BigDecimal amount, PaymentMethod paymentMethod);

    Payment processPayment(Long paymentId);

    Payment getPaymentByOrderId(Long orderId);

    Payment getPaymentByOrderNumber(String orderNumber);
}

