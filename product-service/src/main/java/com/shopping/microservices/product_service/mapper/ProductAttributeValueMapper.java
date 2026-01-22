package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductAttributeValueMapper {

    /**
     * Map ProductAttributeValue entity to ProductAttributeValueDTO
     */
    public ProductAttributeValueDTO toDTO(ProductAttributeValue value) {
        if (value == null) return null;

        ProductAttribute attribute = value.getProductAttribute();
        String attributeGroupName = null;
        if (attribute != null && attribute.getProductAttributeGroup() != null) {
            attributeGroupName = attribute.getProductAttributeGroup().getName();
        }

        return new ProductAttributeValueDTO(
                attribute != null ? attribute.getId() : null,
                attribute != null ? attribute.getName() : null,
                attributeGroupName,
                value.getValue()
        );
    }

    /**
     * Map list of ProductAttributeValues to list of ProductAttributeValueDTOs
     */
    public List<ProductAttributeValueDTO> toDTOList(List<ProductAttributeValue> values) {
        if (values == null) return Collections.emptyList();
        return values.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
