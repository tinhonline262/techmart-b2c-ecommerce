package com.shopping.microservices.product_service.dto.template;

import java.io.Serializable;
import java.util.List;

public record TemplateAttributesAssignmentDTO(
        List<AttributeAssignmentDTO> attributes) implements Serializable {

    public record AttributeAssignmentDTO(
            Long attributeId,
            Integer displayOrder) implements Serializable {
    }
}
