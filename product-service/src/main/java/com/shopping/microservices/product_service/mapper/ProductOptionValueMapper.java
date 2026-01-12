package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.entity.ProductOptionValue;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductOptionValueMapper {

    /**
     * Map ProductOptionValue entity to ProductOptionValueDTO
     */
    public ProductOptionValueDTO toDTO(ProductOptionValue value) {
        if (value == null) return null;

        ProductOption option = value.getProductOption();
        return new ProductOptionValueDTO(
                value.getId(),
                option != null ? option.getId() : null,
                option != null ? option.getName() : null,
                value.getValue(),
                value.getDisplayType(),
                value.getDisplayOrder(),
                null, // createdAt
                null  // updatedAt
        );
    }

    /**
     * Map list of ProductOptionValues to list of ProductOptionValueDTOs
     */
    public List<ProductOptionValueDTO> toDTOList(List<ProductOptionValue> values) {
        if (values == null) return Collections.emptyList();
        return values.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
