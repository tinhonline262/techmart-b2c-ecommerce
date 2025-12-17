package com.shopping.microservices.order_service.servive;

import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.product.ProductDTO;
import com.shopping.microservices.order_service.dto.product.ReduceStockProductDTO;
import jakarta.validation.Valid;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@FeignClient(name = "product-service")
public interface ProductService {
    @GetMapping("/api/v1/product/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id);
    @GetMapping("/api/v1/product/sku/{sku}")
    public ApiResponse<ProductDTO> getProductBySku(@PathVariable String sku);
    @PutMapping("/api/v1/product/sku/reverse")
    public ApiResponse<ProductDTO> reverseProductStockBySku(@Valid @RequestBody ReduceStockProductDTO reduceStockProductDTO);
}
