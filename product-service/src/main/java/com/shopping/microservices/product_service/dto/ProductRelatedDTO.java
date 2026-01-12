package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductRelatedDTO(
        Long id,
        String name,
        String slug,
        BigDecimal price,
        BigDecimal oldPrice,
        String thumbnailUrl,
        Double averageRating,
        Integer reviewCount,
        boolean inStock
) implements Serializable {
}
