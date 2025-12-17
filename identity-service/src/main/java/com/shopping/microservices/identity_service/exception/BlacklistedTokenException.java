package com.shopping.microservices.identity_service.exception;

public class BlacklistedTokenException extends RuntimeException {
    public BlacklistedTokenException(String message) {
        super(message);
    }
}
