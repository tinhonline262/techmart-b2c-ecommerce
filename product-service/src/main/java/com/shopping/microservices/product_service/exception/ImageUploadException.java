package com.shopping.microservices.product_service.exception;

public class ImageUploadException extends ApplicationException {
    public ImageUploadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ImageUploadException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
