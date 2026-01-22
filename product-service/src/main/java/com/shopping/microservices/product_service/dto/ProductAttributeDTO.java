package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public record ProductAttributeDTO(
        Long id,
        String name,
        Long groupId,
        String groupName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
