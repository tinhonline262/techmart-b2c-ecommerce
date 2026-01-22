package com.shopping.microservices.product_service.exception;

public class BrandNotFoundException extends RuntimeException {
    private final Long brandId;
    
    public BrandNotFoundException(Long brandId) {
        super("Brand not found: " + brandId);
        this.brandId = brandId;
    }
    
    public BrandNotFoundException(String message) {
        super(message);
        this.brandId = null;
    }
}
