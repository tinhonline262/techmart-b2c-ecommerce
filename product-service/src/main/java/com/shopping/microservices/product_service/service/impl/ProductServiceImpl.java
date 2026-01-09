package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;
import com.shopping.microservices.product_service.dto.ProductReduceStockDTO;
import com.shopping.microservices.product_service.exception.CategoryNotFoundException;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductMapper;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
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
    private final ProductMapper productMapper;

    @Cacheable(value = "productById", key = "#id")
    @Transactional(readOnly = true)
    public ProductDTO findById(long id) {
        log.info("Finding product by id: {}", id);
        var productFound = productRepository.findById(id)
                .map(productMapper::mapToDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + id));
        log.info("Product found: {}", productFound.name());
        return productFound;
    }

    @Cacheable(value = "productBySku", key = "#sku")
    @Override
    @Transactional(readOnly = true)
    public ProductDTO findBySku(String sku) {
        log.info("Finding product by SKU: {}", sku);
        var productFound = productRepository.findBySku(sku)
                .map(productMapper::mapToDTO)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        log.info("Product found: {}", productFound.sku());
        return productFound;
    }

    @Cacheable(value = "products_all")
    public List<ProductDTO> findAll() {
        log.info("Finding all products");
        var products = productRepository.findAll().stream()
                .map(productMapper::mapToDTO)
                .collect(Collectors.toList());
        log.info("Products found: {}", products.size());
        return products;
    }

    @Override
    @Transactional
    @CacheEvict(value = "products_all", allEntries = true)
    public ProductDTO createProduct(ProductCreationDTO productCreationDTO) {
        log.info("Creating product: {}", productCreationDTO.name());
        var categoryOfProduct = categoryRepository.findById(productCreationDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found: " + productCreationDTO.categoryId()));
        var product = productMapper.mapToEntity(productCreationDTO, categoryOfProduct);
        var savedProduct = productRepository.save(product);
        log.info("Product created: {}", savedProduct.getId());
        return productMapper.mapToDTO(savedProduct);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"productBySku", "products_all"}, allEntries = true)
    public ProductDTO reverseProductStockBySku(ProductReduceStockDTO productDTO) {
        log.info("Reversing product by SKU: {}", productDTO.sku());
        var product = productRepository.findBySku(productDTO.sku())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + productDTO.sku()));
        productRepository.save(product);
        log.info("Product stock reversed for SKU: {}", productDTO.sku());
        return null;
    }
}
