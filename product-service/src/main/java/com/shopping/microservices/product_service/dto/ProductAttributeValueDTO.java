package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public record ProductAttributeValueDTO(
        Long id,
        Long attributeId,
        String attributeName,
        String value,
        String displayType,
        Integer displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
