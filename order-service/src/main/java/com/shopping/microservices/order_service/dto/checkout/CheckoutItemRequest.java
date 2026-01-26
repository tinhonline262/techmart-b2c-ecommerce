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

        String description,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
