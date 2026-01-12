package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductOptionCreationDTO;
import com.shopping.microservices.product_service.dto.ProductOptionDTO;
import com.shopping.microservices.product_service.dto.ProductOptionUpdateDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductOption;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductOptionMapper {

    private final ProductOptionValueMapper productOptionValueMapper;

    /**
     * Map ProductOptionCreationDTO to ProductOption entity
     */
    public ProductOption toEntity(ProductOptionCreationDTO dto, Product product) {
        if (dto == null) return null;

        return ProductOption.builder()
                .product(product)
                .name(dto.name())
                .build();
    }

    /**
     * Update ProductOption entity from ProductOptionUpdateDTO
     */
    public void updateEntity(ProductOption option, ProductOptionUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) option.setName(dto.name());
    }

    /**
     * Map ProductOption entity to ProductOptionDTO
     */
    public ProductOptionDTO toDTO(ProductOption option) {
        if (option == null) return null;

        return new ProductOptionDTO(
                option.getId(),
                option.getName(),
                Collections.emptyList(), // values - to be loaded separately
                null, // createdAt
                null  // updatedAt
        );
    }

    /**
     * Map list of ProductOptions to list of ProductOptionDTOs
     */
    public List<ProductOptionDTO> toDTOList(List<ProductOption> options) {
        if (options == null) return Collections.emptyList();
        return options.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
