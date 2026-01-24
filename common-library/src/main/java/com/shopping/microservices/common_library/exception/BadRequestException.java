package com.shopping.microservices.common_library.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for bad request scenarios.
 *
 * Default HTTP status: 400 BAD_REQUEST
 * Used when client sends invalid data or malformed requests.
 */
public class BadRequestException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final HttpStatus DEFAULT_STATUS = HttpStatus.BAD_REQUEST;

    /**
     * Constructor with message
     *
     * @param message Error message
     */
    public BadRequestException(String message) {
        super(message, "BAD_REQUEST", DEFAULT_STATUS);
    }

    /**
     * Constructor with message and error code
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     */
    public BadRequestException(String message, String errorCode) {
        super(message, errorCode, DEFAULT_STATUS);
    }

    /**
     * Constructor with message, error code, and HTTP status
     *
     * @param message Error message
     * @param errorCode Application-specific error code
     * @param httpStatus HTTP status code
     */
    public BadRequestException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    /**
     * Constructor with message and cause
     *
     * @param message Error message
     * @param cause Original exception
     */
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, "BAD_REQUEST", DEFAULT_STATUS);
    }

    /**
     * Constructor with message, cause, and error code
     *
     * @param message Error message
     * @param cause Original exception
     * @param errorCode Application-specific error code
     */
    public BadRequestException(String message, Throwable cause, String errorCode) {
        super(message, cause, errorCode, DEFAULT_STATUS);
    }

    /**
     * Factory method for invalid parameter
     *
     * @param parameterName Name of the invalid parameter
     * @param reason Reason why it's invalid
     * @return BadRequestException with formatted message
     */
    public static BadRequestException invalidParameter(String parameterName, String reason) {
        return new BadRequestException(
                String.format("Invalid parameter '%s': %s", parameterName, reason),
                "INVALID_PARAMETER"
        );
    }

    /**
     * Factory method for missing required field
     *
     * @param fieldName Name of the missing field
     * @return BadRequestException with formatted message
     */
    public static BadRequestException missingField(String fieldName) {
        return new BadRequestException(
                String.format("Required field '%s' is missing", fieldName),
                "MISSING_REQUIRED_FIELD"
        );
    }

    /**
     * Factory method for invalid format
     *
     * @param fieldName Name of the field
     * @param expectedFormat Expected format
     * @return BadRequestException with formatted message
     */
    public static BadRequestException invalidFormat(String fieldName, String expectedFormat) {
        return new BadRequestException(
                String.format("Field '%s' has invalid format. Expected: %s", fieldName, expectedFormat),
                "INVALID_FORMAT"
        );
    }
}