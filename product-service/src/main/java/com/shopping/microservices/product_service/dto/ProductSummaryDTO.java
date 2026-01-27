package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductSummaryDTO(
        Long id,
        String name,
        String slug,
        String sku,
        BigDecimal price,
        String description,
        BigDecimal oldPrice,
        BigDecimal specialPrice,
        String thumbnailUrl,
        String brandName,
        Integer stockQuantity,
        boolean inStock,
        boolean isPublished,
        boolean isFeatured,
        Double averageRating,
        Integer reviewCount
) implements Serializable {
}
