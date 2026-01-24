package com.shopping.microservices.order_service.dto.checkout;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record CheckoutItemResponse(
        Long id,
        Long productId,
        String name,
        String description,
        Integer quantity,
        BigDecimal price,
        BigDecimal tax,
        BigDecimal shipmentFee,
        BigDecimal shipmentTax,
        BigDecimal discountAmount,
        BigDecimal subtotal,
        String checkoutId
) {
}
