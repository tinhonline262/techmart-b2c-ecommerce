package com.shopping.microservices.product_service.dto.brand;

import java.io.Serializable;

public record BrandDetailDTO(
        Long id,
        String name,
        String slug,
        boolean isPublished,
        int productCount
) implements Serializable {
}
