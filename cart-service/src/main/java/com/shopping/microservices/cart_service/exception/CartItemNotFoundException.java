package com.shopping.microservices.cart_service.exception;

/**
 * Exception thrown when a cart item is not found
 */
public class CartItemNotFoundException extends RuntimeException {
    
    public CartItemNotFoundException(String customerId, Long productId) {
        super(String.format("Cart item not found for customer: %s, productId: %d", customerId, productId));
    }
    
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
