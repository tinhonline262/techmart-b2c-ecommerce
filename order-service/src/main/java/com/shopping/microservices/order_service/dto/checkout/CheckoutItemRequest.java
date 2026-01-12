package com.shopping.microservices.order_service.dto.checkout;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CheckoutItemRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        BigDecimal tax,

        BigDecimal shipmentFee,

        BigDecimal shipmentTax,

        BigDecimal discountAmount
) {
}
