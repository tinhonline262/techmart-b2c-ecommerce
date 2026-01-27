package com.shopping.microservices.order_service.service;

import com.shopping.microservices.order_service.config.FeignClientConfig;
import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.PageResponseDTO;
import com.shopping.microservices.order_service.dto.product.ProductDTO;
import com.shopping.microservices.order_service.dto.product.ProductSummaryDTO;
import com.shopping.microservices.order_service.dto.product.ReduceStockProductDTO;
import jakarta.validation.Valid;
import org.checkerframework.checker.units.qual.A;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name = "product-service", configuration = FeignClientConfig.class)
public interface ProductService {
    @GetMapping("/api/v1/products/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id);
    @GetMapping("/api/v1/products/sku/{sku}")
    public ApiResponse<ProductDTO> getProductBySku(@PathVariable String sku);
    @PutMapping("/api/v1/products/sku/reverse")
    public ApiResponse<ProductDTO> reverseProductStockBySku(@Valid @RequestBody ReduceStockProductDTO reduceStockProductDTO);
    
    /**
     * Fetches product information for multiple products in bulk.
     * 
     * @param productIds Set of product IDs to fetch
     * @return Map of product ID to ProductDTO
     */
    @GetMapping("/api/v1/products")
    public ApiResponse<PageResponseDTO<ProductSummaryDTO>> getProductInformation(
            @RequestParam("ids") List<Long> productIds
    );
}
