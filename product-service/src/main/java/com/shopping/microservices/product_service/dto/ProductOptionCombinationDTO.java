package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductOptionCombinationDTO(
        Long id,
        Long productId,
        String sku,
        BigDecimal price,
        Integer stockQuantity,
        String thumbnailUrl,
        List<ProductOptionValueDTO> optionValues,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
