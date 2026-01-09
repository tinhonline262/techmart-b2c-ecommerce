package com.shopping.microservices.inventory_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event for order confirmation
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

