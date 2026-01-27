package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record ProductAttributeGroupDTO(
        Long id,
        String name,
        List<ProductAttributeDTO> attributes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}