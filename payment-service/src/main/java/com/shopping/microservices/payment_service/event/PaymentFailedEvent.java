package com.shopping.microservices.payment_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Event published when payment fails
 */
@Builder
public record PaymentFailedEvent(
        Long orderId,
        String orderNumber,
        Long paymentId,
        BigDecimal amount,
        String paymentMethod,
        String reason
) implements Serializable {
}

