package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductAttributeGroupMapper {

    private final ProductAttributeMapper productAttributeMapper;

    public ProductAttributeGroupDTO toDTO(ProductAttributeGroup group) {
        if (group == null) {
            return null;
        }

        List<ProductAttributeDTO> attributes = group.getProductAttributes() != null
                ? group.getProductAttributes().stream()
                        .map(productAttributeMapper::toDTO)
                        .collect(Collectors.toList())
                : List.of();

        LocalDateTime createdAt = null;
        if (group.getCreatedAt() != null) {
            createdAt = LocalDateTime.ofInstant(group.getCreatedAt(), ZoneId.systemDefault());
        }

        LocalDateTime updatedAt = null;
        if (group.getUpdatedAt() != null) {
            updatedAt = LocalDateTime.ofInstant(group.getUpdatedAt(), ZoneId.systemDefault());
        }

        return new ProductAttributeGroupDTO(
                group.getId(),
                group.getName(),
                attributes,
                createdAt,
                updatedAt);
    }

    public List<ProductAttributeGroupDTO> toDTOList(List<ProductAttributeGroup> groups) {
        return groups.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
