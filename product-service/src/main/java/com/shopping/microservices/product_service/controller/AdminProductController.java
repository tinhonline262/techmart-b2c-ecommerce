package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.client.InventoryServiceClient;
import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.repository.*;
import com.shopping.microservices.product_service.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin Product Controller
 * Handles all administrative operations for products and options.
 * Base path: /api/v1/products
 */
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final ProductOptionService productOptionService;
    private final ProductOptionValueService productOptionValueService;
    private final ProductAttributeService productAttributeService;
    private final ProductAttributeValueService productAttributeValueService;
    private final ProductAttributeGroupService productAttributeGroupService;
    private final InventoryServiceClient inventoryServiceClient;

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
        var allProducts = productService.findAll();

        // Apply filters
        var filtered = allProducts.stream()
                .filter(p -> ids == null || ids.isEmpty() || ids.contains(p.id()))
                .filter(p -> categoryIds == null || categoryIds.isEmpty() || (p.categories() != null && p.categories().stream().anyMatch(c -> categoryIds.contains(c.id()))))
                .filter(p -> brandIds == null || brandIds.isEmpty() || brandIds.contains(p.brand().id()))
                .filter(p -> keyword == null || keyword.isEmpty() || p.name().toLowerCase().contains(keyword.toLowerCase()))
                .filter(p -> minPrice == null || p.price().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.price().compareTo(maxPrice) <= 0)
                .filter(p -> isPublished == null || p.isPublished() == isPublished)
                .filter(p -> isFeatured == null || p.isFeatured() == isFeatured)
                .filter(p -> inStock == null || (inStock && p.stockQuantity() != null && p.stockQuantity() > 0) || (!inStock && (p.stockQuantity() == null || p.stockQuantity() <= 0)))
                .toList();

        // Create paginated response
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, filtered.size());

        List<ProductDTO> pageContent = start < filtered.size() ? filtered.subList(start, end) : List.of();
        var pageResponse = new PageResponseDTO<>(
                pageContent,
                pageNumber,
                pageSize,
                (long) filtered.size(),
                (filtered.size() + pageSize - 1) / pageSize,
                pageNumber == 0,
                end >= filtered.size(),
                pageContent.isEmpty()
        );

        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Products retrieved successfully"));
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
        var allProducts = productService.findAll();
        var latest = allProducts.stream()
                .sorted((a, b) -> b.createdAt() != null && a.createdAt() != null ? b.createdAt().compareTo(a.createdAt()) : 0)
                .limit(limit)
                .toList();
        return ResponseEntity.ok(ApiResponse.success(latest, "Latest products retrieved successfully"));
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
        var allProducts = productService.findAll();

        // Apply search filters
        var searched = allProducts.stream()
                .filter(p -> keyword == null || keyword.isEmpty() ||
                        p.name().toLowerCase().contains(keyword.toLowerCase()) ||
                        (p.description() != null && p.description().toLowerCase().contains(keyword.toLowerCase())) ||
                        (p.sku() != null && p.sku().toLowerCase().contains(keyword.toLowerCase())))
                .filter(p -> categoryIds == null || categoryIds.isEmpty() || (p.categories() != null && p.categories().stream().anyMatch(c -> categoryIds.contains(c.id()))))
                .filter(p -> brandIds == null || brandIds.isEmpty() || brandIds.contains(p.brand().id()))
                .filter(p -> minPrice == null || p.price().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.price().compareTo(maxPrice) <= 0)
                .filter(p -> inStock == null || (inStock && p.stockQuantity() != null && p.stockQuantity() > 0) || (!inStock && (p.stockQuantity() == null || p.stockQuantity() <= 0)))
                .filter(p -> isPublished == null || p.isPublished() == isPublished)
                .toList();

        // Create paginated response
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, searched.size());

        List<ProductDTO> pageContent = start < searched.size() ? searched.subList(start, end) : List.of();
        PageResponseDTO<ProductDTO> pageResponse = new PageResponseDTO<>(
                pageContent,
                pageNumber,
                pageSize,
                (long) searched.size(),
                (searched.size() + pageSize - 1) / pageSize,
                pageNumber == 0,
                end >= searched.size(),
                pageContent.isEmpty()
        );

        ApiResponse<PageResponseDTO<ProductDTO>> response = ApiResponse.success(pageResponse, "Product search completed successfully");
        return ResponseEntity.ok(response);
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
        try {
            // Get inventory data from inventory-service
            PageResponseDTO<WarehouseProductDTO> inventoryPage;

            if (outOfStock != null && outOfStock) {
                var response = inventoryServiceClient.getOutOfStockInventory(pageable);
                inventoryPage = convertInventoryToWarehouseProducts(response.getData());
            } else if (lowStock != null && lowStock) {
                var response = inventoryServiceClient.getLowStockInventory(pageable);
                inventoryPage = convertInventoryToWarehouseProducts(response.getData());
            } else {
                var response = inventoryServiceClient.getAllInventory(pageable);
                inventoryPage = convertInventoryToWarehouseProducts(response.getData());
            }

            // Filter by SKU if provided
            if (sku != null && !sku.isEmpty()) {
                var filtered = inventoryPage.content().stream()
                        .filter(p -> p.sku() != null && p.sku().toLowerCase().contains(sku.toLowerCase()))
                        .toList();
                var filteredResponse = new PageResponseDTO<>(
                        filtered,
                        inventoryPage.pageNumber(),
                        inventoryPage.pageSize(),
                        inventoryPage.totalElements(),
                        inventoryPage.totalPages(),
                        inventoryPage.first(),
                        inventoryPage.last(),
                        filtered.isEmpty()
                );
                return ResponseEntity.ok(ApiResponse.success(filteredResponse, "Warehouse products retrieved successfully"));
            }

            return ResponseEntity.ok(ApiResponse.success(inventoryPage, "Warehouse products retrieved successfully"));
        } catch (Exception e) {
            // Fallback: return empty response if inventory-service is unavailable
            var emptyResponse = new PageResponseDTO<WarehouseProductDTO>(
                    List.of(),
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    0,
                    0,
                    true,
                    true,
                    true
            );
            return ResponseEntity.ok(ApiResponse.success(emptyResponse, "Warehouse products retrieved successfully (inventory service unavailable)"));
        }
    }

    private PageResponseDTO<WarehouseProductDTO> convertInventoryToWarehouseProducts(PageResponseDTO<InventoryDTO> inventoryPage) {
        var allProducts = productService.findAll();

        var warehouseProducts = inventoryPage.content().stream()
                .map(inventory -> {
                    var product = allProducts.stream()
                            .filter(p -> p.id().equals(inventory.productId()))
                            .findFirst();

                    if (product.isPresent()) {
                        var p = product.get();
                        // Null-safe calculation
                        long availableQty = (inventory.quantity() != null ? inventory.quantity() : 0) -
                                (inventory.reservedQuantity() != null ? inventory.reservedQuantity() : 0);
                        return new WarehouseProductDTO(
                                p.id(),
                                p.name(),
                                inventory.sku(),
                                inventory.quantity() != null ? inventory.quantity().intValue() : 0,
                                inventory.reservedQuantity() != null ? inventory.reservedQuantity().intValue() : 0,
                                (int) availableQty,
                                p.cost(),
                                "Warehouse " + inventory.warehouseId(),
                                inventory.quantity() != null && inventory.quantity() <= 10,
                                10,
                                20
                        );
                    }
                    return null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        return new PageResponseDTO<>(
                warehouseProducts,
                inventoryPage.pageNumber(),
                inventoryPage.pageSize(),
                (long) warehouseProducts.size(),
                (int) Math.ceil((double) warehouseProducts.size() / (inventoryPage.pageSize() > 0 ? inventoryPage.pageSize() : 1)),
                inventoryPage.pageNumber() == 0,
                true,
                warehouseProducts.isEmpty()
        );
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
        var allProducts = productService.findAll();

        // Apply filters
        var filtered = allProducts.stream()
                .filter(p -> ids == null || ids.isEmpty() || ids.contains(p.id()))
                .filter(p -> categoryIds == null || categoryIds.isEmpty() || (p.categories() != null && p.categories().stream().anyMatch(c -> categoryIds.contains(c.id()))))
                .filter(p -> brandIds == null || brandIds.isEmpty() || brandIds.contains(p.brand().id()))
                .toList();

        // Generate CSV export
        byte[] csvBytes = exportToCSV(filtered);

        return ResponseEntity.ok(ApiResponse.success(csvBytes, "Products exported successfully"));
    }

    private byte[] exportToCSV(List<ProductDTO> products) {
        StringBuilder csv = new StringBuilder();

        // CSV Header
        csv.append("ID,Name,SKU,Price,Stock Quantity,Published,Featured,Brand ID,Cost\n");

        // CSV Data
        for (ProductDTO product : products) {
            csv.append(product.id()).append(",");
            csv.append("\"").append(escapeCsvValue(product.name())).append("\",");
            csv.append(product.sku() != null ? "\"" + escapeCsvValue(product.sku()) + "\"" : "\"\"").append(",");
            csv.append(product.price()).append(",");
            csv.append(product.stockQuantity() != null ? product.stockQuantity() : "0").append(",");
            csv.append(product.isPublished()).append(",");
            csv.append(product.isFeatured()).append(",");
            csv.append(product.brand() != null && product.brand().id() != null ? product.brand().id() : "\"\"").append(",");
            csv.append(product.cost()).append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escapeCsvValue(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\"", "\"\"");
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
        var created = productService.createProduct(productCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Product created successfully"));
    }

    /**
     * Get product by ID.
     *
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        var product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(product, "Product retrieved successfully"));
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
        var updated = productService.updateProduct(id, productUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product updated successfully"));
    }

    /**
     * Delete product by ID.
     *
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
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
        var updated = productService.updateProductQuantity(id, inventoryUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product quantity updated successfully"));
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
        var updated = productService.subtractProductQuantity(id, inventorySubtractDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product quantity subtracted successfully"));
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
        var result = productAttributeService.getAttributes(pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attributes retrieved successfully"));
    }

    /**
     * Get product attribute by ID.
     *
     * GET /api/v1/products/attributes/{id}
     */
    @GetMapping("/attributes/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> getAttributeById(@PathVariable Long id) {
        var result = productAttributeService.getAttributeById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute retrieved successfully"));
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
        var result = productAttributeService.createAttribute(attributeCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result, "Product attribute created successfully"));
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
        var result = productAttributeService.updateAttribute(id, attributeUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute updated successfully"));
    }

    /**
     * Delete product attribute by ID.
     *
     * DELETE /api/v1/products/attributes/{id}
     */
    @DeleteMapping("/attributes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteAttribute(@PathVariable Long id) {
        productAttributeService.deleteAttribute(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get product attribute values by attribute ID.
     *
     * GET /api/v1/products/attribute-values?attributeId=1
     */
    @GetMapping("/attribute-values")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductAttributeValueDTO>>> getAttributeValues(
            @RequestParam Long attributeId) {
        var result = productAttributeValueService.getAttributeValues(attributeId);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute values retrieved successfully"));
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
        var result = productAttributeGroupService.getAttributeGroups(pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute groups retrieved successfully"));
    }

    /**
     * Get product attribute group by ID.
     *
     * GET /api/v1/products/attribute-groups/{id}
     */
    @GetMapping("/attribute-groups/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> getAttributeGroupById(@PathVariable Long id) {
        var result = productAttributeGroupService.getAttributeGroupById(id);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute group retrieved successfully"));
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
        var result = productAttributeGroupService.createAttributeGroup(attributeGroupCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(result, "Product attribute group created successfully"));
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
        var result = productAttributeGroupService.updateAttributeGroup(id, attributeGroupUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(result, "Product attribute group updated successfully"));
    }

    /**
     * Delete product attribute group by ID.
     *
     * DELETE /api/v1/products/attribute-groups/{id}
     */
    @DeleteMapping("/attribute-groups/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteAttributeGroup(@PathVariable Long id) {
        productAttributeGroupService.deleteAttributeGroup(id);
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
        var options = productOptionService.getOptions(pageable);
        return ResponseEntity.ok(ApiResponse.success(options, "Product options retrieved successfully"));
    }

    /**
     * Get product option by ID.
     *
     * GET /api/v1/products/options/{id}
     */
    @GetMapping("/options/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductOptionDTO>> getOptionById(@PathVariable Long id) {
        var option = productOptionService.getOptionById(id);
        return ResponseEntity.ok(ApiResponse.success(option, "Product option retrieved successfully"));
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
        var created = productOptionService.createOption(optionCreationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Product option created successfully"));
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
        var updated = productOptionService.updateOption(id, optionUpdateDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Product option updated successfully"));
    }

    /**
     * Delete product option by ID.
     *
     * DELETE /api/v1/products/options/{id}
     */
    @DeleteMapping("/options/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<ApiResponse<Void>> deleteOption(@PathVariable Long id) {
        productOptionService.deleteOption(id);
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
            @RequestParam(required = true) Long optionId) {
        var values = productOptionValueService.getOptionValues(optionId);
        return ResponseEntity.ok(ApiResponse.success(values, "Product option values retrieved successfully"));
    }
}
