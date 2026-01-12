package com.shopping.microservices.product_service.dto;

import java.io.Serializable;

public record ProductAttributeValueDTO(
        Long attributeId,
        String attributeName,
        String attributeGroupName,
        String value
) implements Serializable {
}
