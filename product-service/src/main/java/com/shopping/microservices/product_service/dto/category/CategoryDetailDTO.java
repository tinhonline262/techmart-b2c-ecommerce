package com.shopping.microservices.product_service.dto.category;

import com.shopping.microservices.product_service.entity.Category;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record CategoryDetailDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long parentId,
        String parentName,
        String metaDescription,
        String metaKeywords,
        Integer displayOrder,
        boolean isPublished,
        int productCount
) implements Serializable {
    public static CategoryDetailDTO toDTO(Category category, String imageUrl, int productCount) {
        if (category.getParent() != null) {
            return CategoryDetailDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .slug(category.getSlug())
                    .description(category.getDescription())
                    .imageUrl(imageUrl)
                    .parentId(category.getParent().getId())
                    .parentName(category.getParent().getName())
                    .metaDescription(category.getMetaDescription())
                    .metaKeywords(category.getMetaKeyword())
                    .displayOrder(category.getDisplayOrder())
                    .isPublished(category.getIsPublished())
                    .productCount(productCount)
                    .build();
        } else {
            return CategoryDetailDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .slug(category.getSlug())
                    .description(category.getDescription())
                    .imageUrl(imageUrl)
                    .parentId(null)
                    .parentName(null)
                    .metaDescription(category.getMetaDescription())
                    .metaKeywords(category.getMetaKeyword())
                    .displayOrder(category.getDisplayOrder())
                    .isPublished(category.getIsPublished())
                    .productCount(productCount)
                    .build();
        }
    }
}
