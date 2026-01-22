package com.shopping.microservices.common_library.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for all custom exceptions in the microservices.
 * 
 * Provides a standard structure with error code and HTTP status
 * for consistent exception handling across services.
 */
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Application-specific error code for client-side handling
     */
    private final String errorCode;

    /**
     * HTTP status to return in the response
     */
    private final HttpStatus httpStatus;

    /**
     * Constructor with message, error code, and HTTP status
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     * @param httpStatus HTTP status code
     */
    public BaseException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructor with message, cause, error code, and HTTP status
     *
     * @param message Error message
     * @param cause Original exception
     * @param errorCode Application-specific error code
     * @param httpStatus HTTP status code
     */
    public BaseException(String message, Throwable cause, String errorCode, HttpStatus httpStatus) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * Constructor with just message and HTTP status (auto-generates error code)
     *
     * @param message Error message
     * @param httpStatus HTTP status code
     */
    public BaseException(String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = "ERR_" + httpStatus.value();
        this.httpStatus = httpStatus;
    }
}
