package com.shopping.microservices.order_service.event.consumer;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Event received when payment fails
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

