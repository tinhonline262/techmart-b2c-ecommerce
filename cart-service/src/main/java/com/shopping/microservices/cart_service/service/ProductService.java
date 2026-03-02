package com.shopping.microservices.cart_service.service;

import com.shopping.microservices.cart_service.dto.ProductDTO;

import java.util.List;
import java.util.Map;

/**
 * Service interface for fetching product information from Product Service
 */
public interface ProductService {

    /**
     * Get a single product by its ID
     *
     * @param productId the product ID
     * @return ProductDTO or null if not found
     */
    ProductDTO getProductById(Long productId);

    /**
     * Get multiple products by their IDs (bulk fetch)
     *
     * @param productIds list of product IDs
     * @return Map of productId to ProductDTO
     */
    Map<Long, ProductDTO> getProductsByIds(List<Long> productIds);
}
