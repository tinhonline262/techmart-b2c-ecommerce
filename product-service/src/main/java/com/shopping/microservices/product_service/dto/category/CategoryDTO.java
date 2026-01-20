package com.shopping.microservices.product_service.dto.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.product_service.entity.Category;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Category response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        String parentName,
        Integer displayOrder,
        boolean isPublished
) implements Serializable {
    public static CategoryDTO toDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .displayOrder(category.getDisplayOrder())
                .isPublished(category.getIsPublished())
                .build();
    }
}