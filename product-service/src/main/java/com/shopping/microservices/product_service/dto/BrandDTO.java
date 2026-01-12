package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BrandDTO(
        Long id,
        String name,
        String slug,
        String description,
        String logoUrl,
        boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
