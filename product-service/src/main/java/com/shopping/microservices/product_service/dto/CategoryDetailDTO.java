package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.util.List;

public record CategoryDetailDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long parentId,
        String parentName,
        List<CategoryDTO> children,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        Integer displayOrder,
        boolean isPublished,
        int productCount
) implements Serializable {
}
