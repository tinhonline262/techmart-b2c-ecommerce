package com.shopping.microservices.product_service.dto.category;

import java.io.Serializable;
import java.util.List;

public record CategorySuggestionDTO(
        Long id,
        String name,
        String slug,
        String imageUrl,
        Long parentId,
        String parentName,
        List<CategorySuggestionDTO> children,
        int productCount
) implements Serializable {
}
