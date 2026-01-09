package com.shopping.microservices.order_service.event.producer;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when order is cancelled
 */
@Builder
public record OrderCancelledEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        String customerName,
        String customerEmail,
        Instant cancelledAt,
        BigDecimal totalAmount,
        String reason
) implements Serializable {
}

