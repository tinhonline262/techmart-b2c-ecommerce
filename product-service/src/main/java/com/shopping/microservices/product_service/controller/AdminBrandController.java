package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.dto.brand.BrandCreationDTO;
import com.shopping.microservices.product_service.dto.brand.BrandDTO;
import com.shopping.microservices.product_service.dto.brand.BrandUpdateDTO;
import com.shopping.microservices.product_service.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin Brand Controller
 * Handles all administrative operations for product brands.
 * Base path: /api/v1/brands
 */
@RestController
@RequestMapping("/api/v1/brands")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    /**
     * Get all brands with optional pagination.
     * Supports bulk queries by ids.
     * 
     * GET /api/v1/brands
     * GET /api/v1/brands?ids=1,2,3
     * GET /api/v1/brands?page=0&size=10
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<BrandDTO>>> getBrands(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        PageResponseDTO<BrandDTO> brands = brandService.getAllBrands(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(brands, "Brands retrieved successfully"));
    }

    /**
     * Get brand by ID.
     * 
     * GET /api/v1/brands/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<BrandDTO>> getBrandById(@PathVariable Long id) {
        BrandDTO brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand retrieved successfully"));
    }

    /**
     * Create a new brand.
     * 
     * POST /api/v1/brands
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<BrandDTO>> createBrand(
            @Valid @RequestBody BrandCreationDTO brandCreationDTO) {
        BrandDTO brand = brandService.createBrand(brandCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(brand, "Brand created successfully"));
    }

    /**
     * Update brand by ID.
     * 
     * PUT /api/v1/brands/{id}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<BrandDTO>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandUpdateDTO brandUpdateDTO) {
        BrandDTO brand = brandService.updateBrand(id, brandUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand updated successfully"));
    }

    /**
     * Delete brand by ID.
     * 
     * DELETE /api/v1/brands/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
}
