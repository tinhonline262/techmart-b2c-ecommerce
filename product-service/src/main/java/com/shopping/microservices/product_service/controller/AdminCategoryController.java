package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.dto.category.CategoryCreationDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDetailDTO;
import com.shopping.microservices.product_service.dto.category.CategoryUpdateDTO;
import com.shopping.microservices.product_service.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Category Controller
 * Handles all administrative operations for product categories.
 * Base path: /api/v1/categories
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    /**
     * Get all categories with optional pagination and keyword search.
     * 
     * GET /api/v1/categories
     * GET /api/v1/categories?keyword=electronics
     * GET /api/v1/categories?page=0&size=10
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<CategoryDTO>>> getCategories(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {

        PageResponseDTO<CategoryDTO> categories = categoryService.getCategories(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved successfully"));
    }

    /**
     * Get category by ID.
     * 
     * GET /api/v1/categories/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<CategoryDetailDTO>> getCategoryById(@PathVariable Long id) {
        CategoryDetailDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(category, "Category retrieved successfully"));
    }

    /**
     * Create a new category.
     * 
     * POST /api/v1/categories
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(
            @Valid @RequestBody CategoryCreationDTO categoryCreationDTO) {
        CategoryDTO createdCategory = categoryService.createCategory(categoryCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdCategory, "Category created successfully"));
    }

    /**
     * Update category by ID.
     * 
     * PUT /api/v1/categories/{id}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(updatedCategory, "Category updated successfully"));
    }

    /**
     * Delete category by ID.
     * 
     * DELETE /api/v1/categories/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
