package com.shopping.microservices.identity_service.exception;

public class SignUpNotValidException extends RuntimeException {
    public SignUpNotValidException(String message) {
        super(message);
    }
}
