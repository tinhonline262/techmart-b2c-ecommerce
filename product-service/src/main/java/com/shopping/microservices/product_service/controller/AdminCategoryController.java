package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Category Controller
 * Handles all administrative operations for product categories.
 * Base path: /api/v1/categories
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    /**
     * Get all categories with optional pagination.
     * Supports bulk queries by ids.
     * 
     * GET /api/v1/categories
     * GET /api/v1/categories?ids=1,2,3
     * GET /api/v1/categories?page=0&size=10
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<CategoryDTO>>> getCategories(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Categories retrieved successfully"));
    }

    /**
     * Get category by ID.
     * 
     * GET /api/v1/categories/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<CategoryDetailDTO>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "Category retrieved successfully"));
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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Category created successfully"));
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
        return ResponseEntity.ok(ApiResponse.success(null, "Category updated successfully"));
    }

    /**
     * Delete category by ID.
     * 
     * DELETE /api/v1/categories/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
