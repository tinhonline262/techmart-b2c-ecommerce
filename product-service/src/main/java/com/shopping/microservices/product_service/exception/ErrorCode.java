package com.shopping.microservices.product_service.exception;


public enum ErrorCode {
    RESOURCE_NOT_FOUND("The requested resource was not found."),
    UNKNOWN_ERROR("Unknown error!"),
    INVALID_IMAGE_FILE("Invalid image file. Supported formats: JPG, PNG, GIF, WebP"),
    IMAGE_FILE_TOO_LARGE("Image file exceeds maximum allowed size"),
    IMAGE_UPLOAD_FAILED("Failed to upload image to Cloudinary"),
    IMAGE_NOT_FOUND("Image not found"),
    IMAGE_DELETE_FAILED("Failed to delete image from Cloudinary");

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