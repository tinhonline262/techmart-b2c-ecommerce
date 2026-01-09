package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.ApiResponse;
import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;
import com.shopping.microservices.product_service.dto.ProductReduceStockDTO;
import com.shopping.microservices.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.OutputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<ProductDTO> getProductById(@PathVariable Long id) {
        return ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully", productService.findById(id));
    }

    @GetMapping("sku/{sku}")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<ProductDTO> getProductBySku(@PathVariable String sku) {
        return ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully", productService.findBySku(sku));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<List<ProductDTO>> getAllProducts() {
        return ApiResponse.success(HttpStatus.OK.value(), "Product retrieved successfully", productService.findAll());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<ProductDTO> createProduct(@Valid @RequestBody ProductCreationDTO productCreationDTO) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Product created successfully", productService.createProduct(productCreationDTO));
    }

    @PutMapping("sku/reverse")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<ProductDTO> reverseProductStockBySku(@RequestBody @Valid ProductReduceStockDTO productReduceStockDTO) {
        return ApiResponse.success(HttpStatus.OK.value(), "Product stock reversed successfully", productService.reverseProductStockBySku(productReduceStockDTO));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(OutputStream outputStream) {
        return ResponseEntity.ok("OK");
    }
}
