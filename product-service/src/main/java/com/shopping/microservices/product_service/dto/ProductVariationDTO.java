package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductVariationDTO(
        Long id,
        String sku,
        BigDecimal price,
        BigDecimal oldPrice,
        Integer stockQuantity,
        boolean inStock,
        String thumbnailUrl,
        java.util.List<ProductOptionValueDTO> optionValues
) implements Serializable {
}
