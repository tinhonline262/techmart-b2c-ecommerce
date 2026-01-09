package com.shopping.microservices.payment_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event for Order Created in Saga pattern
 * This event is published when an order is created and needs to be processed through the saga orchestration
 */
@Builder
public record OrderCreatedEvent(
        Long orderId,
        String orderNumber,
        Long customerId,
        String customerName,
        String customerEmail,
        Instant orderDate,
        String status,
        BigDecimal totalAmount,
        List<OrderItemData> orderItems,
        String paymentMethod
) implements Serializable {

    /**
     * Data transfer object for order items within the saga event
     */
    @Builder
    public record OrderItemData(
            Long productId,
            String sku,
            String productName,
            Integer quantity,
            BigDecimal price,
            BigDecimal subtotal
    ) implements Serializable {
    }
}

