package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductAttributeGroupCreationDTO(
        @NotBlank(message = "Attribute group name is required")
        @Size(max = 255, message = "Attribute group name must not exceed 255 characters")
        String name,
        @NotNull(message = "Product ID is required")
        Long productId

) implements Serializable {
}
