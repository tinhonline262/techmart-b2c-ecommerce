package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductAttributeCreationDTO(
        @NotBlank(message = "Attribute name is required")
        @Size(max = 255, message = "Attribute name must not exceed 255 characters")
        String name,

        Long groupId
) implements Serializable {
}
