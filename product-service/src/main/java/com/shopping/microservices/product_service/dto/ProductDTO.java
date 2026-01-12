package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Product response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDTO(
        Long id,
        String name,
        String slug,
        String sku,
        String shortDescription,
        String description,
        String specification,
        BigDecimal price,
        BigDecimal oldPrice,
        BigDecimal specialPrice,
        BigDecimal cost,
        Integer stockQuantity,
        boolean stockTrackingEnabled,
        boolean isAllowedToOrder,
        boolean isPublished,
        boolean isFeatured,
        boolean isVisibleIndividually,
        BrandDTO brand,
        List<CategoryDTO> categories,
        List<ProductImageDTO> images,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        String thumbnailUrl,
        BigDecimal weight,
        String dimensions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}