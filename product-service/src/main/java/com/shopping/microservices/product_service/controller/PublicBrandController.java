package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.repository.BrandRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Public Brand Controller
 * Handles all customer-facing read operations for brands.
 * Base path: /api/v1/public/brands
 */
@RestController
@RequestMapping("/api/v1/public/brands")
@RequiredArgsConstructor
public class PublicBrandController {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    /**
     * Get all published brands.
     * 
     * GET /api/v1/public/brands
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<BrandDTO>>> getBrands() {
        return ResponseEntity.ok(ApiResponse.success(null, "Brands retrieved successfully"));
    }

    /**
     * Get brand by slug (SEO-friendly URL).
     * 
     * GET /api/v1/public/brands/{brandSlug}
     */
    @GetMapping("/{brandSlug}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<BrandDetailDTO>> getBrandBySlug(@PathVariable String brandSlug) {
        return ResponseEntity.ok(ApiResponse.success(null, "Brand retrieved by slug successfully"));
    }

    /**
     * Get products by brand slug with filtering and pagination.
     * 
     * GET /api/v1/public/brands/{brandSlug}/products
     */
    @GetMapping("/{brandSlug}/products")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductSummaryDTO>>> getProductsByBrand(
            @PathVariable String brandSlug,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Products by brand retrieved successfully"));
    }
}
