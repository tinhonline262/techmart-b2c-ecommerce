package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record FeaturedProductDTO(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal oldPrice,
        BigDecimal specialPrice,
        String thumbnailUrl,
        String brandName,
        Double averageRating,
        Integer reviewCount,
        boolean inStock,
        boolean hasDiscount
) implements Serializable {
}
