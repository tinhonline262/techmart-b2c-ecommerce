package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.exception.CategoryNotFoundException;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductMapper;
import com.shopping.microservices.product_service.entity.ProductCategory;
import com.shopping.microservices.product_service.repository.*;
import com.shopping.microservices.product_service.service.ProductService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

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
}