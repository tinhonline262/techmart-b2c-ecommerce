package com.shopping.microservices.identity_service.exception;

public class LoginNotValidException extends RuntimeException {
    public LoginNotValidException(String message) {
        super(message);
    }
}
