package com.shopping.microservices.order_service.dto.order;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record OrderSummaryResponse(
        Long id,
        String email,
        Integer numberItem,
        BigDecimal totalAmount,
        String status,
        String paymentStatus,
        String shipmentStatus,
        String customerId,
        Instant createdAt
) {
}
