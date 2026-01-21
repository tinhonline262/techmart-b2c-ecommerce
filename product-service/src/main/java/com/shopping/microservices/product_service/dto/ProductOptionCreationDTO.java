package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductOptionCreationDTO(
        @NotNull(message = "Product ID is required")
        Long productId,
        @NotBlank(message = "Option name is required")
        @Size(max = 255, message = "Option name must not exceed 255 characters")
        String name
) implements Serializable {
}
