package com.shopping.microservices.order_service.event.consumer;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Event received when payment is completed successfully
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

