package com.shopping.microservices.product_service.dto.brand;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record BrandUpdateDTO(
        @Size(max = 255, message = "Brand name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        Boolean isPublished
) implements Serializable {
}
