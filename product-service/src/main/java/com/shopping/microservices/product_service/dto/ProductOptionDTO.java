package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record ProductOptionDTO(
        Long id,
        String name,
        List<ProductOptionValueDTO> values,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
