package com.shopping.microservices.product_service.dto.attribute;

import java.io.Serializable;

public record ProductAttributeValueCreationDTO(
        Long attributeId,
        String value) implements Serializable {
}
