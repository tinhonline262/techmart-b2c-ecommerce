package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductImageCreationDTO;
import com.shopping.microservices.product_service.dto.ProductImageDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductImageMapper {

    /**
     * Map ProductImageCreationDTO to ProductImage entity
     */
    public ProductImage toEntity(ProductImageCreationDTO dto, Product product) {
        if (dto == null) return null;

        return ProductImage.builder()
                .product(product)
                .imageId(0L) // Placeholder - should be set from media service
                .displayOrder(dto.displayOrder() != null ? dto.displayOrder() : 0)
                .isPrimary(dto.isPrimary())
                .build();
    }

    /**
     * Map ProductImage entity to ProductImageDTO
     */
    public ProductImageDTO toDTO(ProductImage image) {
        if (image == null) return null;

        return new ProductImageDTO(
                image.getId(),
                image.getProduct() != null ? image.getProduct().getId() : null,
                null, // imageUrl - should be fetched from media service using imageId
                null, // altText
                Boolean.TRUE.equals(image.getIsPrimary()),
                image.getDisplayOrder(),
                null, // createdAt
                null  // updatedAt
        );
    }

    /**
     * Map list of ProductImages to list of ProductImageDTOs
     */
    public List<ProductImageDTO> toDTOList(List<ProductImage> images) {
        if (images == null) return Collections.emptyList();
        return images.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
