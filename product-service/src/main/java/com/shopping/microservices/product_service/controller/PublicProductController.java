package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.service.ProductService;
import com.shopping.microservices.product_service.service.ProductAttributeService;
import com.shopping.microservices.product_service.service.ProductAttributeGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Public Product Controller
 * Handles all customer-facing read operations for products.
 * Base path: /api/v1/public/products
 */
@RestController
@RequestMapping("/api/v1/public/products")
@RequiredArgsConstructor
@Slf4j
public class PublicProductController {

    private final ProductService productService;
    private final ProductAttributeService productAttributeService;
    private final ProductAttributeGroupService productAttributeGroupService;

    /**
     * Get all published products with multi-criteria filtering.
     * GET /api/v1/public/products
     * GET
     * /api/v1/public/products?categoryIds=1,2&brandIds=1&minPrice=10&maxPrice=100
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductSummaryDTO>>> getProducts(
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection,
            Pageable pageable) {

        Page<ProductSummaryDTO> productPage = productService.findPublishedProducts(
                categoryIds, brandIds, minPrice, maxPrice, inStock, sortBy, sortDirection, pageable);

        PageResponseDTO<ProductSummaryDTO> pageResponse = new PageResponseDTO<>(
                productPage.getContent(),
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast(),
                productPage.isEmpty());

        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Products retrieved successfully"));
    }

    /**
     * Get featured products for homepage or promotional displays.
     * 
     * GET /api/v1/public/products/featured
     */
    @GetMapping("/featured")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<FeaturedProductDTO>>> getFeaturedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<FeaturedProductDTO> featuredProducts = productService.findFeaturedProducts(limit);
        return ResponseEntity.ok(ApiResponse.success(featuredProducts, "Featured products retrieved successfully"));
    }

    /**
     * Get featured products by specific IDs.
     * 
     * GET /api/v1/public/products/featured-by-ids?ids=1,2,3
     */
    @GetMapping("/featured-by-ids")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<FeaturedProductDTO>>> getFeaturedProductsByIds(
            @RequestParam List<Long> ids) {
        List<FeaturedProductDTO> products = productService.findFeaturedProductsByIds(ids);
        return ResponseEntity.ok(ApiResponse.success(products, "Featured products by IDs retrieved successfully"));
    }

    /**
     * Get product summary by ID.
     * 
     * GET /api/v1/public/products/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductSummaryDTO>> getProductById(@PathVariable Long id) {
        ProductSummaryDTO product = productService.findPublishedProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
    }

    /**
     * Get product full detail by ID (includes images, attributes, categories,
     * brand).
     * 
     * GET /api/v1/public/products/{id}/detail
     */
    @GetMapping("/{id}/detail")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductDetail(@PathVariable Long id) {
        ProductDetailDTO productDetail = productService.findPublishedProductDetailById(id);
        return ResponseEntity.ok(ApiResponse.success(productDetail, "Product detail retrieved successfully"));
    }

    /**
     * Get product variations/SKU combinations by product ID.
     * 
     * GET /api/v1/public/products/{id}/variations
     */
    @GetMapping("/{id}/variations")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductVariationDTO>>> getProductVariations(@PathVariable Long id) {
        log.info("Fetching product variations for product ID: {}", id);
        List<ProductVariationDTO> variations = productService.findProductVariations(id);
        return ResponseEntity.ok(ApiResponse.success(variations, "Product variations retrieved successfully"));
    }

    /**
     * Get related products by product ID.
     * 
     * GET /api/v1/public/products/{id}/related
     * GET /api/v1/public/products/{id}/related?limit=10
     */
    @GetMapping("/{id}/related")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductRelatedDTO>>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching related products for product ID: {} with limit: {}", id, limit);
        List<ProductRelatedDTO> relatedProducts = productService.findRelatedProducts(id, limit);
        return ResponseEntity.ok(ApiResponse.success(relatedProducts, "Related products retrieved successfully"));
    }

    /**
     * Get product by slug (SEO-friendly URLs).
     * 
     * GET /api/v1/public/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDetailDTO>> getProductBySlug(@PathVariable String slug) {
        log.info("Fetching product by slug: {}", slug);
        ProductDetailDTO product = productService.findPublishedProductBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved by slug successfully"));
    }

    /**
     * Get product slug by ID (for URL generation).
     * 
     * GET /api/v1/public/products/{id}/slug
     */
    @GetMapping("/{id}/slug")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<String>> getProductSlug(@PathVariable Long id) {
        log.info("Fetching product slug for product ID: {}", id);
        String slug = productService.getProductSlugById(id);
        return ResponseEntity.ok(ApiResponse.success(slug, "Product slug retrieved successfully"));
    }

    /**
     * Get product attributes for filtering (public facing).
     * 
     * GET /api/v1/public/products/attributes
     * GET /api/v1/public/products/attributes?page=0&size=10
     */
    @GetMapping("/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeDTO>>> getAttributes(Pageable pageable) {
        log.info("Fetching product attributes for public API");
        PageResponseDTO<ProductAttributeDTO> attributes = productAttributeService.getAttributes(pageable);
        return ResponseEntity.ok(ApiResponse.success(attributes, "Product attributes retrieved successfully"));
    }

    /**
     * Get product attribute groups for filtering (public facing).
     * 
     * GET /api/v1/public/products/attribute-groups
     * GET /api/v1/public/products/attribute-groups?page=0&size=10
     */
    @GetMapping("/attribute-groups")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeGroupDTO>>> getAttributeGroups(
            Pageable pageable) {
        log.info("Fetching product attribute groups for public API");
        PageResponseDTO<ProductAttributeGroupDTO> groups = productAttributeGroupService.getAttributeGroups(pageable);
        return ResponseEntity.ok(ApiResponse.success(groups, "Product attribute groups retrieved successfully"));
    }

    /**
     * Get option values for a specific product.
     * 
     * GET /api/v1/public/products/{productId}/option-values
     */
    @GetMapping("/{productId}/option-values")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> getProductOptionValues(
            @PathVariable Long productId) {
        log.info("Fetching product option values for product ID: {}", productId);
        List<ProductOptionValueDTO> optionValues = productService.getProductOptionValues(productId);
        return ResponseEntity.ok(ApiResponse.success(optionValues, "Product option values retrieved successfully"));
    }

    /**
     * Get option combinations (SKU variants) for a specific product.
     * 
     * GET /api/v1/public/products/{productId}/option-combinations
     */
    @GetMapping("/{productId}/option-combinations")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductOptionCombinationDTO>>> getProductOptionCombinations(
            @PathVariable Long productId) {
        log.info("Fetching product option combinations for product ID: {}", productId);
        List<ProductOptionCombinationDTO> combinations = productService.getProductOptionCombinations(productId);
        return ResponseEntity
                .ok(ApiResponse.success(combinations, "Product option combinations retrieved successfully"));
    }
}