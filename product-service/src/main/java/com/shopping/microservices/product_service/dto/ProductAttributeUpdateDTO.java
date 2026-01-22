package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductAttributeUpdateDTO(
        @Size(max = 255, message = "Attribute name must not exceed 255 characters")
        String name,

        Long groupId
) implements Serializable {
}