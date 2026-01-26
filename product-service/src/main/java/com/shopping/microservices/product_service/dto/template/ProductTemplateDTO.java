package com.shopping.microservices.product_service.dto.template;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public record ProductTemplateDTO(
        Long id,
        String name,
        List<ProductAttributeTemplateDTO> attributes,
        Instant createdAt,
        Instant updatedAt) implements Serializable {
}
