package com.shopping.microservices.product_service.dto;

import com.shopping.microservices.product_service.dto.brand.BrandDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailDTO(
        Long id,
        String name,
        String slug,
        String sku,
        String shortDescription,
        String description,
        String specification,
        BigDecimal price,
        BigDecimal oldPrice,
        BigDecimal specialPrice,
        Integer stockQuantity,
        boolean inStock,
        boolean isPublished,
        boolean isFeatured,
        BrandDTO brand,
        List<CategoryDTO> categories,
        List<ProductImageDTO> images,
        List<ProductOptionDTO> options,
        List<ProductAttributeValueDTO> attributes,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        String thumbnailUrl,
        BigDecimal weight,
        String dimensions,
        Double averageRating,
        Integer reviewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
