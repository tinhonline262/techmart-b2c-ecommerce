package com.shopping.microservices.cart_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record ProductSummaryDTO(
        Long id,
        String name,
        String slug,
        String sku,
        BigDecimal price,
        String description
) implements Serializable {
}
