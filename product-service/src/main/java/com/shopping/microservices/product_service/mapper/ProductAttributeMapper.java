package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductAttributeMapper {

    /**
     * Map ProductAttributeCreationDTO to ProductAttribute entity
     */
    public ProductAttribute toEntity(ProductAttributeCreationDTO dto, ProductAttributeGroup group) {
        if (dto == null) return null;

        ProductAttribute attribute = new ProductAttribute();
        attribute.setName(dto.name());
        attribute.setProductAttributeGroup(group);
        attribute.setCreatedAt(Instant.now());
        attribute.setUpdatedAt(Instant.now());
        return attribute;
    }

    /**
     * Update ProductAttribute entity from ProductAttributeUpdateDTO
     */
    public void updateEntity(ProductAttribute attribute, ProductAttributeUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) {
            attribute.setName(dto.name());
        }
        if (dto.groupId() != null) {
            // groupId will be set via service - mapper doesn't have access to repository
        }
        attribute.setUpdatedAt(Instant.now());
    }

    /**
     * Map ProductAttribute entity to ProductAttributeDTO
     */
    public ProductAttributeDTO toDTO(ProductAttribute attribute) {
        if (attribute == null) return null;

        return new ProductAttributeDTO(
                attribute.getId(),
                attribute.getName(),
                attribute.getProductAttributeGroup() != null ? attribute.getProductAttributeGroup().getId() : null,
                attribute.getProductAttributeGroup() != null ? attribute.getProductAttributeGroup().getName() : null,
                convertInstantToLocalDateTime(attribute.getCreatedAt()),
                convertInstantToLocalDateTime(attribute.getUpdatedAt())
        );
    }

    /**
     * Map list of ProductAttributes to list of ProductAttributeDTOs
     */
    public List<ProductAttributeDTO> toDTOList(List<ProductAttribute> attributes) {
        if (attributes == null) return Collections.emptyList();
        return attributes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Instant to LocalDateTime
     */
    private LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
