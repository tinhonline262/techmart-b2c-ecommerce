package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
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
public class ProductAttributeGroupMapper {

    private final ProductAttributeMapper attributeMapper;

    public ProductAttributeGroupMapper(ProductAttributeMapper attributeMapper) {
        this.attributeMapper = attributeMapper;
    }

    /**
     * Map ProductAttributeGroupCreationDTO to ProductAttributeGroup entity
     */
    public ProductAttributeGroup toEntity(ProductAttributeGroupCreationDTO dto) {
        if (dto == null) return null;

        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName(dto.name());
        group.setCreatedAt(Instant.now());
        group.setUpdatedAt(Instant.now());
        return group;
    }

    /**
     * Update ProductAttributeGroup entity from ProductAttributeGroupUpdateDTO
     */
    public void updateEntity(ProductAttributeGroup group, ProductAttributeGroupUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) {
            group.setName(dto.name());
        }
        group.setUpdatedAt(Instant.now());
    }

    /**
     * Map ProductAttributeGroup entity to ProductAttributeGroupDTO
     */
    public ProductAttributeGroupDTO toDTO(ProductAttributeGroup group, List<ProductAttribute> attributes) {
        if (group == null) return null;

        List<ProductAttributeDTO> attributeDTOs = attributes != null
                ? attributes.stream().map(attributeMapper::toDTO).collect(Collectors.toList())
                : Collections.emptyList();

        return new ProductAttributeGroupDTO(
                group.getId(),
                group.getName(),
                attributeDTOs,
                convertInstantToLocalDateTime(group.getCreatedAt()),
                convertInstantToLocalDateTime(group.getUpdatedAt())
        );
    }

    /**
     * Convert Instant to LocalDateTime
     */
    private LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}