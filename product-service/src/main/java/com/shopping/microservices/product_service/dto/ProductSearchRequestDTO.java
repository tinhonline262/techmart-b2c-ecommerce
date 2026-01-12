package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record ProductSearchRequestDTO(
        String keyword,
        List<Long> categoryIds,
        List<Long> brandIds,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Boolean inStock,
        Boolean isFeatured,
        Boolean isPublished,
        String sortBy,
        String sortDirection
) implements Serializable {
}
