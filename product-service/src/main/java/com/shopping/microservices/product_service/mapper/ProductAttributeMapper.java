package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductAttributeMapper {

    private final ProductAttributeGroupRepository productAttributeGroupRepository;

    public ProductAttribute toEntity(ProductAttributeDTO dto) {
        if (dto == null) {
            return null;
        }

        ProductAttribute attribute = new ProductAttribute();
        attribute.setName(dto.name());

        if (dto.groupId() != null) {
            ProductAttributeGroup group = productAttributeGroupRepository.findById(dto.groupId())
                    .orElse(null);
            attribute.setProductAttributeGroup(group);
        }

        return attribute;
    }

    public ProductAttribute updateEntity(ProductAttributeDTO dto, ProductAttribute attribute) {
        if (dto == null || attribute == null) {
            return attribute;
        }

        attribute.setName(dto.name());

        if (dto.groupId() != null) {
            ProductAttributeGroup group = productAttributeGroupRepository.findById(dto.groupId())
                    .orElse(null);
            attribute.setProductAttributeGroup(group);
        } else {
            attribute.setProductAttributeGroup(null);
        }

        return attribute;
    }

    public ProductAttributeDTO toDTO(ProductAttribute attribute) {
        if (attribute == null) {
            return null;
        }

        Long groupId = null;
        String groupName = null;

        if (attribute.getProductAttributeGroup() != null) {
            groupId = attribute.getProductAttributeGroup().getId();
            groupName = attribute.getProductAttributeGroup().getName();
        }

        LocalDateTime createdAt = null;
        if (attribute.getCreatedAt() != null) {
            createdAt = LocalDateTime.ofInstant(attribute.getCreatedAt(), ZoneId.systemDefault());
        }

        LocalDateTime updatedAt = null;
        if (attribute.getUpdatedAt() != null) {
            updatedAt = LocalDateTime.ofInstant(attribute.getUpdatedAt(), ZoneId.systemDefault());
        }

        return new ProductAttributeDTO(
                attribute.getId(),
                attribute.getName(),
                groupId,
                groupName,
                createdAt,
                updatedAt);
    }

    public List<ProductAttributeDTO> toDTOList(List<ProductAttribute> attributes) {
        return attributes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
