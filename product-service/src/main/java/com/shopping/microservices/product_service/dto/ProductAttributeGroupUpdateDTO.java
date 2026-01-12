package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductAttributeGroupUpdateDTO(
        @Size(max = 255, message = "Attribute group name must not exceed 255 characters")
        String name
) implements Serializable {
}
