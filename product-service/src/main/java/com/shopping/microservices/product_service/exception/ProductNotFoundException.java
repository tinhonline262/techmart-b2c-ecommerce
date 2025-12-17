package com.shopping.microservices.product_service.exception;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException{
    private final Long productId;
    public ProductNotFoundException(Long productId) {
        super("Product not found: " + productId);
        this.productId = productId;
    }

    public ProductNotFoundException(String message) {
        super(message);
        this.productId = null;
    }
}
