package com.shopping.microservices.product_service.dto.brand;

import java.io.Serializable;
import java.time.LocalDateTime;

public record BrandDTO(
        Long id,
        String name,
        String slug,
        boolean isPublished
) implements Serializable {
}
