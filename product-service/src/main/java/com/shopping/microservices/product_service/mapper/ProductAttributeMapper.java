package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductAttributeMapper {

    private final ProductAttributeValueMapper productAttributeValueMapper;

    /**
     * Map ProductAttributeCreationDTO to ProductAttribute entity
     */
    public ProductAttribute toEntity(ProductAttributeCreationDTO dto, Product product) {
        if (dto == null) return null;

        return ProductAttribute.builder()
                .product(product)
                .name(dto.name())
                .build();
    }

    /**
     * Update ProductAttribute entity from ProductAttributeUpdateDTO
     */
    public void updateEntity(ProductAttribute attribute, ProductAttributeUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) attribute.setName(dto.name());
    }

    /**
     * Map ProductAttribute entity to ProductAttributeDTO
     */
    public ProductAttributeDTO toDTO(ProductAttribute attribute) {
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
     * Map list of ProductAttributes to list of ProductAttributeDTOs
     */
    public List<ProductAttributeDTO> toDTOList(List<ProductAttribute> attributes) {
        if (attributes == null) return Collections.emptyList();
        return attributes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
