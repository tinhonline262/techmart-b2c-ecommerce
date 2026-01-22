package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.dto.category.CategoryDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDetailDTO;
import com.shopping.microservices.product_service.dto.category.CategorySuggestionDTO;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
import com.shopping.microservices.product_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Public Category Controller
 * Handles all customer-facing read operations for categories.
 * Base path: /api/v1/public/categories
 */
@RestController
@RequestMapping("/api/v1/public/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Get all published categories.
     * 
     * GET /api/v1/public/categories
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Boolean includeChildren) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllPublishedCategories(parentId, includeChildren), "Categories retrieved successfully"));
    }

    /**
     * Get category suggestions for search/autocomplete.
     * 
     * GET /api/v1/public/categories/suggestions
     */
    @GetMapping("/suggestions")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<CategorySuggestionDTO>>> getCategorySuggestions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategorySuggestions(keyword, limit), "Category suggestions retrieved successfully"));
    }

    /**
     * Get category by slug (SEO-friendly URL).
     * 
     * GET /api/v1/public/categories/{categorySlug}
     */
    @GetMapping("/{categorySlug}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<CategoryDetailDTO>> getCategoryBySlug(@PathVariable String categorySlug) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getCategoryBySlug(categorySlug), "Category retrieved by slug successfully"));
    }

    /**
     * Get products by category slug with filtering and pagination.
     * 
     * GET /api/v1/public/categories/{categorySlug}/products
     */
    @GetMapping("/{categorySlug}/products")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductSummaryDTO>>> getProductsByCategory(
            @PathVariable String categorySlug,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Products by category retrieved successfully"));
    }
}
