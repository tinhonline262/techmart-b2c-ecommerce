package com.shopping.microservices.common_library.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for business rule violations.
 * 
 * Default HTTP status: 400 BAD_REQUEST
 * Used when a business rule is violated (e.g., invalid order state transition).
 */
public class BusinessException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final HttpStatus DEFAULT_STATUS = HttpStatus.BAD_REQUEST;

    /**
     * Constructor with message
     *
     * @param message Error message
     */
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR", DEFAULT_STATUS);
    }

    /**
     * Constructor with message and error code
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     */
    public BusinessException(String message, String errorCode) {
        super(message, errorCode, DEFAULT_STATUS);
    }

    /**
     * Constructor with message, error code, and HTTP status
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     * @param httpStatus HTTP status code
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Original exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause, "BUSINESS_ERROR", DEFAULT_STATUS);
    }

    /**
     * Constructor with message, cause, and error code
     *
     * @param message Error message
     * @param cause Original exception
     * @param errorCode Application-specific error code
     */
    public BusinessException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode, DEFAULT_STATUS);
    }
}
