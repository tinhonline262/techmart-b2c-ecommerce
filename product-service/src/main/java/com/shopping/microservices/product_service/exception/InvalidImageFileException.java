package com.shopping.microservices.product_service.exception;

public class InvalidImageFileException extends ApplicationException {
    public InvalidImageFileException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidImageFileException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
