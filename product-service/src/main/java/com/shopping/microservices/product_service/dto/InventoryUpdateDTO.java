package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record InventoryUpdateDTO(
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity must be non-negative")
        Integer quantity,

        String reason
) implements Serializable {
}
