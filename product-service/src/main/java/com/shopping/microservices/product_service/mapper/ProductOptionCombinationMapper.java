package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductOptionCombinationDTO;
import com.shopping.microservices.product_service.dto.ProductVariationDTO;
import com.shopping.microservices.product_service.entity.ProductOptionCombination;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductOptionCombinationMapper {

    private final ProductOptionValueMapper productOptionValueMapper;

    /**
     * Map ProductOptionCombination entity to ProductOptionCombinationDTO
     */
    public ProductOptionCombinationDTO toDTO(ProductOptionCombination combination) {
        if (combination == null)
            return null;

        return new ProductOptionCombinationDTO(
                combination.getId(),
                combination.getProduct() != null ? combination.getProduct().getId() : null,
                combination.getValue(), // Use value field instead of sku
                null, // price
                null, // stockQuantity
                null, // thumbnailUrl
                Collections.emptyList(), // optionValues - to be loaded separately
                null, // createdAt
                null // updatedAt
        );
    }

    /**
     * Map ProductOptionCombination entity to ProductVariationDTO
     */
    public ProductVariationDTO toVariationDTO(ProductOptionCombination combination) {
        if (combination == null)
            return null;

        return new ProductVariationDTO(
                combination.getId(),
                combination.getValue(), // Use value field instead of sku
                null, // price
                null, // oldPrice
                null, // stockQuantity
                false, // inStock
                null, // thumbnailUrl
                Collections.emptyList() // optionValues
        );
    }

    /**
     * Map list of ProductOptionCombinations to list of ProductOptionCombinationDTOs
     */
    public List<ProductOptionCombinationDTO> toDTOList(List<ProductOptionCombination> combinations) {
        if (combinations == null)
            return Collections.emptyList();
        return combinations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map list of ProductOptionCombinations to list of ProductVariationDTOs
     */
    public List<ProductVariationDTO> toVariationDTOList(List<ProductOptionCombination> combinations) {
        if (combinations == null)
            return Collections.emptyList();
        return combinations.stream()
                .map(this::toVariationDTO)
                .collect(Collectors.toList());
    }
}
