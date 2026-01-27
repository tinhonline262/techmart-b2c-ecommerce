package com.shopping.microservices.common_library.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard error response structure for API error handling.
 * 
 * Follows RFC 7807 Problem Details pattern with additional
 * field-level validation error support.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * HTTP status code
     */
    private Integer status;

    /**
     * HTTP status reason phrase (e.g., "Bad Request", "Not Found")
     */
    private String error;

    /**
     * Human-readable error message
     */
    private String message;

    /**
     * Request path that caused the error
     */
    private String path;

    /**
     * Error code for client-side handling
     */
    private String errorCode;

    /**
     * Trace ID for debugging/logging correlation
     */
    private String traceId;

    /**
     * List of field-level validation errors
     */
    @Builder.Default
    private List<FieldError> errors = new ArrayList<>();

    /**
     * Nested class for field-level validation errors
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Field name that caused the error
         */
        private String field;

        /**
         * Error message for this field
         */
        private String message;

        /**
         * The rejected value
         */
        private Object rejectedValue;

        /**
         * Error code for this specific field error
         */
        private String code;
    }

    /**
     * Add a field error to the list
     */
    public void addFieldError(String field, String message, Object rejectedValue) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(FieldError.builder()
                .field(field)
                .message(message)
                .rejectedValue(rejectedValue)
                .build());
    }

    /**
     * Static factory method for creating error response
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Static factory method for 400 Bad Request
     */
    public static ErrorResponse badRequest(String message, String path) {
        return of(400, "Bad Request", message, path);
    }

    /**
     * Static factory method for 404 Not Found
     */
    public static ErrorResponse notFound(String message, String path) {
        return of(404, "Not Found", message, path);
    }

    /**
     * Static factory method for 500 Internal Server Error
     */
    public static ErrorResponse internalError(String message, String path) {
        return of(500, "Internal Server Error", message, path);
    }

    /**
     * Static factory method for validation errors
     */
    public static ErrorResponse validationError(String path, List<FieldError> fieldErrors) {
        return ErrorResponse.builder()
                .status(400)
                .error("Bad Request")
                .message("Validation failed")
                .path(path)
                .errors(fieldErrors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
