package com.shopping.microservices.product_service.dto;

import java.io.Serializable;

public record BrandDetailDTO(
        Long id,
        String name,
        String slug,
        String description,
        String logoUrl,
        boolean isPublished,
        int productCount
) implements Serializable {
}
