package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.category.*;
import com.shopping.microservices.product_service.entity.Category;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import com.shopping.microservices.product_service.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoryMapper {

    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    /**
     * Map CategoryCreationDTO to Category entity
     */
    public Category toEntity(CategoryCreationDTO dto) {
        if (dto == null) return null;

        Category category = Category.builder()
                .name(dto.name())
                .slug(dto.slug())
                .description(dto.description())
                .displayOrder(dto.displayOrder())
                .isPublished(dto.isPublished())
                .metaKeyword(dto.metaKeywords())
                .metaDescription(dto.metaDescription())
                .build();
        
        // Handle parent relationship
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElse(null);
            category.setParent(parent);
        }
        
        return category;
    }

    /**
     * Update Category entity from CategoryUpdateDTO
     */
    public void updateEntity(Category category, CategoryUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) category.setName(dto.name());
        if (dto.slug() != null) category.setSlug(dto.slug());
        if (dto.description() != null) category.setDescription(dto.description());
        if (dto.displayOrder() != null) category.setDisplayOrder(dto.displayOrder());
        if (dto.isPublished() != null) category.setIsPublished(dto.isPublished());
        if (dto.metaKeywords() != null) category.setMetaKeyword(dto.metaKeywords());
        if (dto.metaDescription() != null) category.setMetaDescription(dto.metaDescription());
        
        // Handle parent relationship update
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElse(null);
            category.setParent(parent);
        }
    }

    /**
     * Map Category entity to CategoryDTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;
        return CategoryDTO.toDTO(category);
    }

    /**
     * Map Category entity to CategoryDetailDTO
     */
    public CategoryDetailDTO toDetailDTO(Category category) {
        if (category == null) return null;
        
        // Get product count for this category
        int productCount = productCategoryRepository.findByCategoryId(category.getId()).size();
        
        // For now, imageUrl is null (placeholder for future image service integration)
        return CategoryDetailDTO.toDTO(category, null, productCount);
    }

    /**
     * Map Category entity to CategorySuggestionDTO
     */
    public CategorySuggestionDTO toSuggestionDTO(Category category) {
        if (category == null) return null;

        return new CategorySuggestionDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                null, // imageUrl
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getName() : null,
                Collections.emptyList(), // children
                0     // productCount
        );
    }

    /**
     * Map list of Categories to list of CategoryDTOs
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if (categories == null) return Collections.emptyList();
        return categories.stream()
                .map(CategoryDTO::toDTO)
                .collect(Collectors.toList());
    }
}
