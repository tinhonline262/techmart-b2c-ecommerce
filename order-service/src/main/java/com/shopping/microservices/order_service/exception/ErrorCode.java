package com.shopping.microservices.order_service.exception;

public enum ErrorCode {
    RESOURCE_NOT_FOUND("The requested resource was not found."),
    UNKNOWN_ERROR("Unknown error!");


    private String message;
    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}