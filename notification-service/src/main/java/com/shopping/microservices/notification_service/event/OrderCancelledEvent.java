package com.shopping.microservices.notification_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event for order cancellation
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
