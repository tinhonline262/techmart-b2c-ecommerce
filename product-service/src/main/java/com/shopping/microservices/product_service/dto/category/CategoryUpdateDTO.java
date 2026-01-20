package com.shopping.microservices.product_service.dto.category;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record CategoryUpdateDTO(
        @Size(max = 255, message = "Category name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Slug must not exceed 255 characters")
        String slug,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        @Size(max = 500, message = "Image URL must not exceed 500 characters")
        String imageUrl,

        Long parentId,

        Integer displayOrder,

        Boolean isPublished,

        @Size(max = 255, message = "Meta title must not exceed 255 characters")
        String metaTitle,

        @Size(max = 500, message = "Meta description must not exceed 500 characters")
        String metaDescription,

        @Size(max = 255, message = "Meta keywords must not exceed 255 characters")
        String metaKeywords
) implements Serializable {
}
