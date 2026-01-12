package com.shopping.microservices.order_service.dto.order;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Builder
public record OrderItemResponse(
        Long id,
        Long productId,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        BigDecimal discountAmount,
        BigDecimal taxAmount,
        BigDecimal taxPercent,
        BigDecimal shipmentFee,
        BigDecimal shipmentTax,
        String status,
        BigDecimal subtotal,
        Map<String, Object> processingState,
        Instant createdAt,
        Instant updatedAt
) {
}
