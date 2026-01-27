package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.exception.CategoryNotFoundException;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductMapper;
import com.shopping.microservices.product_service.mapper.ProductOptionCombinationMapper;
import com.shopping.microservices.product_service.mapper.ProductRelatedMapper;
import com.shopping.microservices.product_service.mapper.ProductOptionValueMapper;
import com.shopping.microservices.product_service.entity.ProductCategory;
import com.shopping.microservices.product_service.repository.*;
import com.shopping.microservices.product_service.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final ProductRelatedRepository productRelatedRepository;
    private final ProductOptionCombinationMapper productOptionCombinationMapper;
    private final ProductRelatedMapper productRelatedMapper;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionValueMapper productOptionValueMapper;

    @Cacheable(value = "productById", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO findById(long id) {
        log.info("Finding product by id: {}", id);
        var productFound = productRepository.findById(id)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        log.info("Product found: {}", productFound.id());
        return productFound;
    }

    @Cacheable(value = "productBySku", key = "#sku")
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findBySku(String sku) {
        log.info("Finding product by SKU: {}", sku);
        var productFound = productRepository.findBySku(sku)
                .map(productMapper::toDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        log.info("Product found: {}", productFound.sku());
        return productFound;
    }

    @Cacheable(value = "products_all")
    public List<ProductDTO> findAll() {
        log.info("Finding all products");
        var products = productRepository.findAll().stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
        log.info("Products found: {}", products.size());
        return products;
    }

    @Override
    @Transactional
    @CacheEvict(value = "products_all", allEntries = true)
    public ProductDTO createProduct(ProductCreationDTO productCreationDTO) {
        log.info("Creating product: {}", productCreationDTO.name());
        var product = productMapper.toEntity(productCreationDTO);
        var savedProduct = productRepository.save(product);

        // Link categories if any
        if (productCreationDTO.categoryIds() != null && !productCreationDTO.categoryIds().isEmpty()) {
            productCreationDTO.categoryIds().forEach(categoryId -> {
                var category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));
                var pc = ProductCategory.builder()
                        .product(savedProduct)
                        .category(category)
                        .build();
                productCategoryRepository.save(pc);
            });
        }

        // Images: creation DTO currently contains image URLs; mapping to existing product_image table requires image IDs.
        if (productCreationDTO.images() != null && !productCreationDTO.images().isEmpty()) {
            log.warn("Product images provided but image persistence by URL is not implemented; skipping images for product {}", savedProduct.getId());
        }

        log.info("Product created: {}", savedProduct.getId());
        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public ProductDTO reverseProductStockBySku(ProductReduceStockDTO productDTO) {
        log.info("Reversing product stock by SKU: {}", productDTO.sku());
        var product = productRepository.findBySku(productDTO.sku())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + productDTO.sku()));

        var current = product.getStockQuantity() != null ? product.getStockQuantity() : 0L;
        product.setStockQuantity(current + productDTO.quantity());
        productRepository.save(product);

        log.info("Product stock reversed for SKU: {} by quantity {}", productDTO.sku(), productDTO.quantity());
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        productMapper.updateEntity(product, productUpdateDTO);
        var saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public void deleteProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        productRepository.delete(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public ProductDTO updateProductQuantity(Long id, InventoryUpdateDTO inventoryUpdateDTO) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        product.setStockQuantity(Long.valueOf(inventoryUpdateDTO.quantity()));
        var saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public void updateProductQuantity(InventoryEvent event) {
        var reservations = event.getReservations();
        for (InventoryEvent.ReservationData reservation : reservations) {
            var product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException("Product not found: " + reservation.getProductId()));
            if (reservation.getQuantityAfterAdjustment() != null) {
                product.setStockQuantity(Long.valueOf(reservation.getQuantityAfterAdjustment()));
            }
            productRepository.save(product);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public ProductDTO subtractProductQuantity(Long id, InventorySubtractDTO inventorySubtractDTO) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));

        if (Boolean.TRUE.equals(product.getStockTrackingEnabled())) {
            var current = product.getStockQuantity() != null ? product.getStockQuantity() : 0L;
            if (current < inventorySubtractDTO.quantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + id);
            }
            product.setStockQuantity(current - inventorySubtractDTO.quantity());
        }

        var saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryDTO> findPublishedProducts(
            List<Long> categoryIds,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            String sortBy,
            String sortDirection,
            Pageable pageable) {

        log.info(
                "Finding published products with filters - categoryIds: {}, brandIds: {}, minPrice: {}, maxPrice: {}, inStock: {}",
                categoryIds, brandIds, minPrice, maxPrice, inStock);

        Specification<com.shopping.microservices.product_service.entity.Product> specification = buildProductSpecification(
                categoryIds, brandIds, minPrice, maxPrice, inStock);

        Page<com.shopping.microservices.product_service.entity.Product> products = productRepository
                .findAll(specification, pageable);

        log.info("Found {} published products", products.getTotalElements());

        return products.map(productMapper::toSummaryDTO);
    }

    private Specification<com.shopping.microservices.product_service.entity.Product> buildProductSpecification(
            List<Long> categoryIds,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock) {

        return Specification.where(isPublished())
                .and(categoryIds != null && !categoryIds.isEmpty() ? inCategories(categoryIds) : null)
                .and(brandIds != null && !brandIds.isEmpty() ? inBrands(brandIds) : null)
                .and(minPrice != null ? priceGreaterThanOrEqual(minPrice) : null)
                .and(maxPrice != null ? priceLessThanOrEqual(maxPrice) : null)
                .and(inStock != null && inStock ? hasStock() : null);
    }

     private Specification<com.shopping.microservices.product_service.entity.Product> isPublished() {
        return (root, query, cb) -> cb.isTrue(root.get("isPublished"));
    }

     private Specification<com.shopping.microservices.product_service.entity.Product> inCategories(
            List<Long> categoryIds) {
        return (root, query, cb) -> {
            // Use a subquery to find products that belong to the specified categories
            var subquery = query.subquery(Long.class);
            var productCategoryRoot = subquery
                    .from(com.shopping.microservices.product_service.entity.ProductCategory.class);

            subquery.select(productCategoryRoot.get("product").get("id"))
                    .where(productCategoryRoot.get("category").get("id").in(categoryIds));

            return root.get("id").in(subquery);
        };
    }

     private Specification<com.shopping.microservices.product_service.entity.Product> inBrands(List<Long> brandIds) {
        return (root, query, cb) -> root.get("brandId").in(brandIds);
    }

    private Specification<com.shopping.microservices.product_service.entity.Product> priceGreaterThanOrEqual(
            BigDecimal minPrice) {
        return (root, query, cb) -> cb.ge(root.get("price"), minPrice);
    }

    /**
     * Specification: Product price must be less than or equal to maxPrice
     */
    private Specification<com.shopping.microservices.product_service.entity.Product> priceLessThanOrEqual(
            BigDecimal maxPrice) {
        return (root, query, cb) -> cb.le(root.get("price"), maxPrice);
    }

    /**
     * Specification: Product must have stock quantity greater than 0
     */
    private Specification<com.shopping.microservices.product_service.entity.Product> hasStock() {
        return (root, query, cb) -> cb.gt(root.get("stockQuantity"), 0L);
    }
    @Override
    @Transactional(readOnly = true)
    public List<FeaturedProductDTO> findFeaturedProducts(int limit) {
        log.info("Finding featured products with limit: {}", limit);

        var products = productRepository.findAll((root, query, cb) -> cb.and(
                cb.isTrue(root.get("isPublished")),
                cb.isTrue(root.get("isFeatured"))),
                org.springframework.data.domain.PageRequest.of(0, limit));

        log.info("Found {} featured products", products.getTotalElements());

        return products.getContent().stream()
                .map(productMapper::toFeaturedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeaturedProductDTO> findFeaturedProductsByIds(List<Long> ids) {
        log.info("Finding featured products by IDs: {}", ids);

        var products = productRepository.findAll((root, query, cb) -> cb.and(
                cb.isTrue(root.get("isPublished")),
                cb.isTrue(root.get("isFeatured")),
                root.get("id").in(ids)));

        log.info("Found {} featured products by IDs", products.size());

        return products.stream()
                .map(productMapper::toFeaturedDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductSummaryDTO findPublishedProductById(Long id) {
        log.info("Finding published product by ID: {}", id);

        var product = productRepository.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("id"), id),
                cb.isTrue(root.get("isPublished"))))
                .orElseThrow(() -> new ProductNotFoundException("Published product not found with ID: " + id));

        log.info("Published product found: {}", product.getId());

        return productMapper.toSummaryDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariationDTO> findProductVariations(Long productId) {
        log.info("Finding product variations for product ID: {}", productId);

        // Verify product exists and is published
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with ID {} is not published", productId);
            throw new ProductNotFoundException("Product is not available");
        }

        // Get all option combinations for this product
        var combinations = productOptionCombinationRepository.findByProductId(productId);
        log.info("Found {} variations for product ID: {}", combinations.size(), productId);

        // Map to ProductVariationDTO
        return combinations.stream()
                .map(productOptionCombinationMapper::toVariationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductRelatedDTO> findRelatedProducts(Long productId, int limit) {
        log.info("Finding related products for product ID: {} with limit: {}", productId, limit);

        // Verify product exists and is published
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with ID {} is not published", productId);
            throw new ProductNotFoundException("Product is not available");
        }

        // Get all related products for this product
        var relatedProducts = productRelatedRepository.findByProductId(productId);
        log.info("Found {} related products for product ID: {}", relatedProducts.size(), productId);

        // Map to ProductRelatedDTO and apply limit
        return relatedProducts.stream()
                .limit(limit)
                .map(productRelatedMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO findPublishedProductBySlug(String slug) {
        log.info("Finding published product by slug: {}", slug);

        var product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with slug: " + slug));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with slug {} is not published", slug);
            throw new ProductNotFoundException("Product is not available");
        }

        log.info("Published product found by slug: {}", slug);
        return productMapper.toDetailDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public String getProductSlugById(Long id) {
        log.info("Getting product slug for product ID: {}", id);

        var product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with ID {} is not published", id);
            throw new ProductNotFoundException("Product is not available");
        }

        String slug = product.getSlug();
        log.info("Product slug found: {} for product ID: {}", slug, id);
        return slug;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionValueDTO> getProductOptionValues(Long productId) {
        log.info("Getting product option values for product ID: {}", productId);

        // Verify product exists and is published
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with ID {} is not published", productId);
            throw new ProductNotFoundException("Product is not available");
        }

        // Get all option values for this product
        var optionValues = productOptionValueRepository.findByProductOptionProductId(productId);
        log.info("Found {} option values for product ID: {}", optionValues.size(), productId);

        // Map to ProductOptionValueDTO
        return optionValues.stream()
                .map(productOptionValueMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionCombinationDTO> getProductOptionCombinations(Long productId) {
        log.info("Getting product option combinations for product ID: {}", productId);

        // Verify product exists and is published
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

        if (!Boolean.TRUE.equals(product.getIsPublished())) {
            log.warn("Product with ID {} is not published", productId);
            throw new ProductNotFoundException("Product is not available");
        }

        // Get all option combinations for this product
        var combinations = productOptionCombinationRepository.findByProductId(productId);
        log.info("Found {} option combinations for product ID: {}", combinations.size(), productId);

        // Map to ProductOptionCombinationDTO
        return combinations.stream()
                .map(productOptionCombinationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductSummaryDTO> getProductSummariesByIds(List<Long> productIds) {
        log.info("Getting product summaries for product IDs: {}", productIds);
        var products = productIds.stream()
                .map(id -> productRepository.findById(id)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id)))
                .map(productMapper::toSummaryDTO)
                .collect(Collectors.toList());
        log.info("Found {} products for provided IDs", products.size());
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDetailDTO findPublishedProductDetailById(Long id) {
        log.info("Finding published product detail by ID: {}", id);

        var product = productRepository.findOne((root, query, cb) -> cb.and(
                cb.equal(root.get("id"), id),
                cb.isTrue(root.get("isPublished"))))
                .orElseThrow(() -> new ProductNotFoundException("Published product not found with ID: " + id));

        log.info("Published product detail found: {}", product.getId());

        return productMapper.toDetailDTO(product);
    }
}