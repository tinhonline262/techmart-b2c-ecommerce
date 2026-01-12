package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for creating a new Product
 */
public record ProductCreationDTO(
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        @NotBlank(message = "SKU is required")
        @Size(max = 255, message = "SKU must not exceed 255 characters")
        String sku,

        @Size(max = 5000, message = "Short description must not exceed 5000 characters")
        String shortDescription,

        String description,

        String specification,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", message = "Price must be positive")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "Old price must be positive")
        BigDecimal oldPrice,

        @DecimalMin(value = "0.0", message = "Special price must be positive")
        BigDecimal specialPrice,

        @DecimalMin(value = "0.0", message = "Cost must be positive")
        BigDecimal cost,

        @Min(value = 0, message = "Stock quantity must be non-negative")
        Integer stockQuantity,

        Boolean stockTrackingEnabled,

        Boolean isAllowedToOrder,

        Boolean isPublished,

        Boolean isFeatured,

        Boolean isVisibleIndividually,

        Long brandId,

        List<Long> categoryIds,

        @Size(max = 255, message = "Meta title must not exceed 255 characters")
        String metaTitle,

        @Size(max = 500, message = "Meta description must not exceed 500 characters")
        String metaDescription,

        @Size(max = 255, message = "Meta keywords must not exceed 255 characters")
        String metaKeywords,

        @Size(max = 500, message = "Thumbnail URL must not exceed 500 characters")
        String thumbnailUrl,

        @DecimalMin(value = "0.0", message = "Weight must be positive")
        BigDecimal weight,

        String dimensions,

        List<ProductImageCreationDTO> images
) implements Serializable {
}