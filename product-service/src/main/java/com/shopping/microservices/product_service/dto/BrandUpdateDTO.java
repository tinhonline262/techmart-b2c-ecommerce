package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record BrandUpdateDTO(
        @Size(max = 255, message = "Brand name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Size(max = 500, message = "Logo URL must not exceed 500 characters")
        String logoUrl,

        Boolean isPublished
) implements Serializable {
}
