package com.shopping.microservices.cart_service.exception;

/**
 * Exception thrown when an invalid quantity is provided
 */
public class InvalidQuantityException extends RuntimeException {
    
    public InvalidQuantityException(Integer quantity) {
        super(String.format("Invalid quantity: %d. Quantity must be greater than 0", quantity));
    }
    
    public InvalidQuantityException(String message) {
        super(message);
    }
}
