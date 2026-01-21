package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttributeMapper {

    /**
     * Map ProductAttributeCreationDTO to ProductOption entity (Attribute)
     */
    public ProductOption toAttributeEntity(ProductAttributeCreationDTO dto) {
        if (dto == null) return null;

        return ProductOption.builder()
                .name(dto.name())
                .build();
    }

    /**
     * Update ProductOption entity (Attribute) from ProductAttributeUpdateDTO
     */
    public void updateAttributeEntity(ProductOption attribute, ProductAttributeUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) {
            attribute.setName(dto.name());
        }
    }

    /**
     * Map ProductOption entity (Attribute) to ProductAttributeDTO
     */
    public ProductAttributeDTO toAttributeDTO(ProductOption attribute) {
        if (attribute == null) return null;

        return new ProductAttributeDTO(
                attribute.getId(),
                attribute.getName(),
                Collections.emptyList(), // values - to be loaded separately
                null, // createdAt
                null  // updatedAt
        );
    }

    /**
     * Map list of ProductOptions to list of ProductAttributeDTOs
     */
    public List<ProductAttributeDTO> toAttributeDTOList(List<ProductOption> attributes) {
        if (attributes == null) return Collections.emptyList();
        return attributes.stream()
                .map(this::toAttributeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map ProductAttributeGroupCreationDTO to ProductOption entity (AttributeGroup)
     */
    public ProductOption toAttributeGroupEntity(ProductAttributeGroupCreationDTO dto) {
        if (dto == null) return null;

        return ProductOption.builder()
                .name(dto.name())
                .build();
    }

    /**
     * Update ProductOption entity (AttributeGroup) from ProductAttributeGroupUpdateDTO
     */
    public void updateAttributeGroupEntity(ProductOption attributeGroup, ProductAttributeGroupUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) {
            attributeGroup.setName(dto.name());
        }
    }

    /**
     * Map ProductOption entity (AttributeGroup) to ProductAttributeGroupDTO
     */
    public ProductAttributeGroupDTO toAttributeGroupDTO(ProductOption attributeGroup) {
        if (attributeGroup == null) return null;

        return new ProductAttributeGroupDTO(
                attributeGroup.getId(),
                attributeGroup.getName(),
                null, // attributeIds - to be loaded separately
                null, // productId
                null  // createdAt
        );
    }

    /**
     * Map list of ProductOptions to list of ProductAttributeGroupDTOs
     */
    public List<ProductAttributeGroupDTO> toAttributeGroupDTOList(List<ProductOption> attributeGroups) {
        if (attributeGroups == null) return Collections.emptyList();
        return attributeGroups.stream()
                .map(this::toAttributeGroupDTO)
                .collect(Collectors.toList());
    }
}
