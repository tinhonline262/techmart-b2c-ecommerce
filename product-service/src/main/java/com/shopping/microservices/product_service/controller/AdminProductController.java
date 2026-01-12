package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.repository.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Admin Product Controller
 * Handles all administrative operations for products, inventory, attributes, attribute groups, and options.
 * Base path: /api/v1/products
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;

    // ================================
    // PRODUCT CRUD OPERATIONS
    // ================================

    /**
     * Get all products with pagination, filtering, and sorting.
     * Supports bulk queries by ids, categoryIds, brandIds.
     * 
     * GET /api/v1/products
     * GET /api/v1/products?ids=1,2,3
     * GET /api/v1/products?categoryIds=1,2
     * GET /api/v1/products?brandIds=1,2
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> getProducts(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean isPublished,
            @RequestParam(required = false) Boolean isFeatured,
            @RequestParam(required = false) Boolean inStock,
            Pageable pageable) {
        // Placeholder response
        return ResponseEntity.ok(ApiResponse.success(null, "Products retrieved successfully"));
    }

    /**
     * Get latest products.
     * 
     * GET /api/v1/products/latest?limit=10
     */
    @GetMapping("/latest")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLatestProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(null, "Latest products retrieved successfully"));
    }

    /**
     * Search products by keyword and filters.
     * 
     * GET /api/v1/products/search
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Boolean isPublished,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product search completed successfully"));
    }

    /**
     * Get warehouse inventory overview.
     * 
     * GET /api/v1/products/warehouse
     */
    @GetMapping("/warehouse")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<WarehouseProductDTO>>> getWarehouseProducts(
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Boolean lowStock,
            @RequestParam(required = false) Boolean outOfStock,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Warehouse products retrieved successfully"));
    }

    /**
     * Export products for external use.
     * 
     * GET /api/v1/products/export
     */
    @GetMapping("/export")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<byte[]>> exportProducts(
            @RequestParam(required = false) List<Long> ids,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> brandIds,
            @RequestParam(defaultValue = "csv") String format) {
        return ResponseEntity.ok(ApiResponse.success(null, "Products exported successfully"));
    }

    /**
     * Create a new product.
     * 
     * POST /api/v1/products
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @Valid @RequestBody ProductCreationDTO productCreationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Product created successfully"));
    }

    /**
     * Get product by ID.
     * 
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product retrieved successfully"));
    }

    /**
     * Update product by ID.
     * 
     * PUT /api/v1/products/{id}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product updated successfully"));
    }

    /**
     * Delete product by ID.
     * 
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    // ================================
    // INVENTORY MANAGEMENT
    // ================================

    /**
     * Update product quantity.
     * 
     * PUT /api/v1/products/{id}/quantity
     */
    @PutMapping("/{id}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductQuantity(
            @PathVariable Long id,
            @Valid @RequestBody InventoryUpdateDTO inventoryUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product quantity updated successfully"));
    }

    /**
     * Subtract product quantity (e.g., for order processing).
     * 
     * POST /api/v1/products/{id}/quantity/subtract
     */
    @PostMapping("/{id}/quantity/subtract")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDTO>> subtractProductQuantity(
            @PathVariable Long id,
            @Valid @RequestBody InventorySubtractDTO inventorySubtractDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product quantity subtracted successfully"));
    }

    // ================================
    // PRODUCT ATTRIBUTES
    // ================================

    /**
     * Get all product attributes with optional pagination.
     * 
     * GET /api/v1/products/attributes
     * GET /api/v1/products/attributes?page=0&size=10
     */
    @GetMapping("/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeDTO>>> getAttributes(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attributes retrieved successfully"));
    }

    /**
     * Get product attribute by ID.
     * 
     * GET /api/v1/products/attributes/{id}
     */
    @GetMapping("/attributes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> getAttributeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attribute retrieved successfully"));
    }

    /**
     * Create a new product attribute.
     * 
     * POST /api/v1/products/attributes
     */
    @PostMapping("/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> createAttribute(
            @Valid @RequestBody ProductAttributeCreationDTO attributeCreationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Product attribute created successfully"));
    }

    /**
     * Update product attribute by ID.
     * 
     * PUT /api/v1/products/attributes/{id}
     */
    @PutMapping("/attributes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> updateAttribute(
            @PathVariable Long id,
            @Valid @RequestBody ProductAttributeUpdateDTO attributeUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attribute updated successfully"));
    }

    /**
     * Delete product attribute by ID.
     * 
     * DELETE /api/v1/products/attributes/{id}
     */
    @DeleteMapping("/attributes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteAttribute(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    // ================================
    // PRODUCT ATTRIBUTE GROUPS
    // ================================

    /**
     * Get all product attribute groups with optional pagination.
     * 
     * GET /api/v1/products/attribute-groups
     * GET /api/v1/products/attribute-groups?page=0&size=10
     */
    @GetMapping("/attribute-groups")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeGroupDTO>>> getAttributeGroups(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attribute groups retrieved successfully"));
    }

    /**
     * Get product attribute group by ID.
     * 
     * GET /api/v1/products/attribute-groups/{id}
     */
    @GetMapping("/attribute-groups/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> getAttributeGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attribute group retrieved successfully"));
    }

    /**
     * Create a new product attribute group.
     * 
     * POST /api/v1/products/attribute-groups
     */
    @PostMapping("/attribute-groups")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> createAttributeGroup(
            @Valid @RequestBody ProductAttributeGroupCreationDTO attributeGroupCreationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Product attribute group created successfully"));
    }

    /**
     * Update product attribute group by ID.
     * 
     * PUT /api/v1/products/attribute-groups/{id}
     */
    @PutMapping("/attribute-groups/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> updateAttributeGroup(
            @PathVariable Long id,
            @Valid @RequestBody ProductAttributeGroupUpdateDTO attributeGroupUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product attribute group updated successfully"));
    }

    /**
     * Delete product attribute group by ID.
     * 
     * DELETE /api/v1/products/attribute-groups/{id}
     */
    @DeleteMapping("/attribute-groups/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteAttributeGroup(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    // ================================
    // PRODUCT OPTIONS
    // ================================

    /**
     * Get all product options with optional pagination.
     * 
     * GET /api/v1/products/options
     * GET /api/v1/products/options?page=0&size=10
     */
    @GetMapping("/options")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductOptionDTO>>> getOptions(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product options retrieved successfully"));
    }

    /**
     * Get product option by ID.
     * 
     * GET /api/v1/products/options/{id}
     */
    @GetMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductOptionDTO>> getOptionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product option retrieved successfully"));
    }

    /**
     * Create a new product option.
     * 
     * POST /api/v1/products/options
     */
    @PostMapping("/options")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductOptionDTO>> createOption(
            @Valid @RequestBody ProductOptionCreationDTO optionCreationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Product option created successfully"));
    }

    /**
     * Update product option by ID.
     * 
     * PUT /api/v1/products/options/{id}
     */
    @PutMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductOptionDTO>> updateOption(
            @PathVariable Long id,
            @Valid @RequestBody ProductOptionUpdateDTO optionUpdateDTO) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product option updated successfully"));
    }

    /**
     * Delete product option by ID.
     * 
     * DELETE /api/v1/products/options/{id}
     */
    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteOption(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    // ================================
    // PRODUCT OPTION VALUES
    // ================================

    /**
     * Get all product option values.
     * 
     * GET /api/v1/products/option-values
     */
    @GetMapping("/option-values")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> getOptionValues(
            @RequestParam(required = false) Long optionId) {
        return ResponseEntity.ok(ApiResponse.success(null, "Product option values retrieved successfully"));
    }
}
