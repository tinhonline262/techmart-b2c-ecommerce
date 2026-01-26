package com.shopping.microservices.common_library.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for access forbidden scenarios.
 *
 * Default HTTP status: 403 FORBIDDEN
 * Used when a user attempts to access a resource they don't have permission for.
 */
public class ForbiddenException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final HttpStatus DEFAULT_STATUS = HttpStatus.FORBIDDEN;

    /**
     * Constructor with message
     *
     * @param message Error message
     */
    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", DEFAULT_STATUS);
    }

    /**
     * Constructor with message and error code
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     */
    public ForbiddenException(String message, String errorCode) {
        super(message, errorCode, DEFAULT_STATUS);
    }

    /**
     * Constructor with message, error code, and HTTP status
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     * @param httpStatus HTTP status code
     */
    public ForbiddenException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Original exception
     */
    public ForbiddenException(String message, Throwable cause) {
        super(message, cause, "FORBIDDEN", DEFAULT_STATUS);
    }

    /**
     * Constructor with message, cause, and error code
     *
     * @param message Error message
     * @param cause Original exception
     * @param errorCode Application-specific error code
     */
    public ForbiddenException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode, DEFAULT_STATUS);
    }

    /**
     * Factory method for creating a ForbiddenException for resource access
     *
     * @param resourceName Type of resource
     * @param resourceId Resource identifier
     * @return ForbiddenException with formatted message
     */
    public static ForbiddenException forResource(String resourceName, Object resourceId) {
        return new ForbiddenException(
                String.format("Access forbidden to %s with id: %s", resourceName, resourceId)
        );
    }

    /**
     * Factory method for creating a ForbiddenException for operation
     *
     * @param operation Operation name
     * @param resourceName Type of resource
     * @return ForbiddenException with formatted message
     */
    public static ForbiddenException forOperation(String operation, String resourceName) {
        return new ForbiddenException(
                String.format("You are not authorized to %s this %s", operation, resourceName)
        );
    }
}