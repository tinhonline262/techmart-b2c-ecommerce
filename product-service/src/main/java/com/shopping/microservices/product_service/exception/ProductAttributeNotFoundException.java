package com.shopping.microservices.product_service.exception;

import lombok.Getter;

@Getter
public class ProductAttributeNotFoundException extends RuntimeException {
    private final Long attributeId;

    public ProductAttributeNotFoundException(Long attributeId) {
        super("Product attribute not found: " + attributeId);
        this.attributeId = attributeId;
    }

    public ProductAttributeNotFoundException(String message) {
        super(message);
        this.attributeId = null;
    }
}
