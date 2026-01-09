package com.shopping.microservices.payment_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Event published when payment is completed successfully
 */
@Builder
public record PaymentCompletedEvent(
        Long orderId,
        String orderNumber,
        Long paymentId,
        BigDecimal amount,
        String paymentMethod,
        String message
) implements Serializable {
}

