package com.shopping.microservices.payment_service.gateway;

import com.shopping.microservices.payment_service.dto.*;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PaymentGateway {
    
    /**
     * Get provider identifier (VNPAY, MOMO, etc.)
     */
    String getProviderId();
    
    /**
     * Get supported payment methods
     */
    List<PaymentMethod> getSupportedMethods();
    
    /**
     * Check if this gateway supports the given payment method
     */
    boolean supports(PaymentMethod method);
    
    /**
     * Initiate payment and return redirect URL or payment details
     */
    InitiatedPayment initiatePayment(Payment payment);
    
    /**
     * Verify payment callback/webhook
     */
    boolean verifyCallback(Map<String, String> params);
    
    /**
     * Process payment callback and extract payment details
     */
    CapturedPayment processCallback(Map<String, String> params);
    
    /**
     * Query payment status from provider
     */
    PaymentStatus queryPaymentStatus(String transactionId);
    
    /**
     * Process refund
     */
    RefundResponse processRefund(Payment payment, BigDecimal amount, String reason);
    
    /**
     * Cancel payment
     */
    boolean cancelPayment(Payment payment);
}
