package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.category.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    /**
     * Get all categories with optional keyword search and pagination
     */
    PageResponseDTO<CategoryDTO> getCategories(String keyword, Pageable pageable);

    /**
     * Get category by ID with detailed information
     */
    CategoryDetailDTO getCategoryById(Long id);

    /**
     * Create a new category
     */
    CategoryDTO createCategory(CategoryCreationDTO categoryCreationDTO);

    /**
     * Update existing category
     */
    CategoryDTO updateCategory(Long id, CategoryUpdateDTO categoryUpdateDTO);

    /**
     * Delete category by ID
     */
    void deleteCategory(Long id);

    // Public category methods

    /**
     * Get all published categories
     */
    List<CategoryDTO> getAllPublishedCategories(Long parentId, Boolean includeChildren);

    /**
     * Get published category by slug with detailed information
     */
    CategoryDetailDTO getCategoryBySlug(String slug);

    /**
     * Get category suggestions for search/autocomplete with hierarchical structure
     */
    List<CategorySuggestionDTO> getCategorySuggestions(String keyword, int limit);
}
