package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO for creating a new ProductImage
 */
public record ProductImageCreationDTO(
        @NotBlank(message = "Image URL is required")
        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        @Size(max = 255, message = "Alt text must not exceed 255 characters")
        String altText,

        boolean isPrimary,

        @PositiveOrZero(message = "Display order must be non-negative")
        Integer displayOrder
) implements Serializable {
}
