package com.shopping.microservices.common_library.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when input validation fails.
 * 
 * Extends BusinessException with support for multiple field errors,
 * allowing detailed validation feedback to clients.
 */
@Getter
public class ValidationException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Map of field names to error messages
     */
    private final Map<String, String> fieldErrors;

    /**
     * Constructor with field errors map
     *
     * @param fieldErrors Map of field names to error messages
     */
    public ValidationException(Map<String, String> fieldErrors) {
        super("Validation failed: " + fieldErrors.size() + " error(s)", "VALIDATION_FAILED");
        this.fieldErrors = Collections.unmodifiableMap(new HashMap<>(fieldErrors));
    }

    /**
     * Constructor with message and field errors map
     *
     * @param message Custom error message
     * @param fieldErrors Map of field names to error messages
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, "VALIDATION_FAILED");
        this.fieldErrors = Collections.unmodifiableMap(new HashMap<>(fieldErrors));
    }

    /**
     * Constructor for single field error
     *
     * @param field Field name
     * @param message Error message
     */
    public ValidationException(String field, String message) {
        super("Validation failed for field: " + field, "VALIDATION_FAILED");
        Map<String, String> errors = new HashMap<>();
        errors.put(field, message);
        this.fieldErrors = Collections.unmodifiableMap(errors);
    }

    /**
     * Constructor with just message (no field errors)
     *
     * @param message Error message
     */
    public ValidationException(String message) {
        super(message, "VALIDATION_FAILED");
        this.fieldErrors = Collections.emptyMap();
    }

    /**
     * Check if there are any field errors
     */
    public boolean hasFieldErrors() {
        return fieldErrors != null && !fieldErrors.isEmpty();
    }

    /**
     * Get error message for a specific field
     */
    public String getFieldError(String field) {
        return fieldErrors.get(field);
    }

    /**
     * Get number of field errors
     */
    public int getErrorCount() {
        return fieldErrors.size();
    }

    /**
     * Builder class for constructing ValidationException with multiple errors
     */
    public static class Builder {
        private final Map<String, String> errors = new HashMap<>();
        private String message;

        public Builder addError(String field, String errorMessage) {
            errors.put(field, errorMessage);
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public ValidationException build() {
            if (message != null) {
                return new ValidationException(message, errors);
            }
            return new ValidationException(errors);
        }

        public void throwIfHasErrors() throws ValidationException {
            if (hasErrors()) {
                throw build();
            }
        }
    }

    /**
     * Create a new builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
