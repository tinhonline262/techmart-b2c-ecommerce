package com.shopping.microservices.cart_service.client;

import com.shopping.microservices.cart_service.config.FeignClientConfig;
import com.shopping.microservices.cart_service.dto.ApiResponse;
import com.shopping.microservices.cart_service.dto.ProductDTO;
import com.shopping.microservices.cart_service.dto.ProductSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client for communicating with the Product Service.
 * Uses Eureka service discovery to locate the product-service.
 */
@FeignClient(
        name = "product-service",
        configuration = FeignClientConfig.class
)
public interface ProductServiceClient {

    /**
     * Get a single product by its ID.
     *
     * @param productId the ID of the product
     * @return ApiResponse containing the product details
     */
    @GetMapping("/api/v1/public/products/{id}")
    ApiResponse<ProductDTO> getProductById(@PathVariable("id") Long productId);

    /**
     * Get multiple products by their IDs (bulk fetch).
     * More efficient for fetching product details for multiple cart items.
     *
     * @param productIds list of product IDs
     * @return ApiResponse containing list of product details
     */
    @GetMapping("/api/v1/products/bulk")
    ApiResponse<List<ProductSummaryDTO>> getProductsByIds(@RequestParam("id") List<Long> productIds);
}
