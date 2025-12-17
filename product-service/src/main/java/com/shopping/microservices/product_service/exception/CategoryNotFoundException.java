package com.shopping.microservices.product_service.exception;

public class CategoryNotFoundException extends RuntimeException {
    private final Long categoryId;
    public CategoryNotFoundException(Long categoryId) {
        super("Category not found: " + categoryId);
        this.categoryId = categoryId;
    }
    public CategoryNotFoundException(String message) {
        super(message);
        this.categoryId = null;
    }
}
