package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for Category response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDTO(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Long parentId,
        String parentName,
        List<CategoryDTO> children,
        Integer displayOrder,
        boolean isPublished,
        String metaTitle,
        String metaDescription,
        String metaKeywords,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}