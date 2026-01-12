package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ProductOptionValueDTO(
        Long id,
        Long optionId,
        String optionName,
        String value,
        String displayType,
        Integer displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
