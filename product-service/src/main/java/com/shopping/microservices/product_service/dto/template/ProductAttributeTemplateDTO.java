package com.shopping.microservices.product_service.dto.template;

import java.io.Serializable;

public record ProductAttributeTemplateDTO(
        Long attributeId,
        String attributeName,
        String groupName,
        Integer displayOrder
) implements Serializable {
}
