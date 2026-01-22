package com.shopping.microservices.cart_service.exception;


public enum ErrorCode {
    RESOURCE_NOT_FOUND("The requested resource was not found."),
    CART_ITEM_NOT_FOUND("Cart item not found."),
    INVALID_QUANTITY("Invalid quantity provided."),
    INVALID_REQUEST("Invalid request data."),
    UNAUTHORIZED("Unauthorized access."),
    DATABASE_ERROR("Database operation failed."),
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