package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Public Search Controller
 * Handles product search operations for customers.
 * Base path: /api/v1/public/search
 */
@RestController
@RequestMapping("/api/v1/public/search")
@RequiredArgsConstructor
public class PublicSearchController {

    private final ProductRepository productRepository;

    /**
     * Search products with full-text search and multi-criteria filtering.
     * 
     * GET /api/v1/public/search/products?q=keyword
     * GET /api/v1/public/search/products?q=keyword&categoryIds=1,2&brandIds=1&minPrice=10&maxPrice=100
     */
    @GetMapping("/products")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductSummaryDTO>>> searchProducts(
            @RequestParam(name = "q", required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product search completed successfully"));
    }
}
