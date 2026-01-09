package com.shopping.microservices.order_service.event.producer;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event published when order is completed successfully
 */
@Builder
public record OrderCompletedEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        String customerName,
        String customerEmail,
        Instant completedAt,
        BigDecimal totalAmount,
        String message
) implements Serializable {
}

