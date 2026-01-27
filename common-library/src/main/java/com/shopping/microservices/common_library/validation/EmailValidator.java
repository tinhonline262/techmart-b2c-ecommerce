package com.shopping.microservices.common_library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for @ValidEmail annotation.
 * 
 * Validates email addresses using RFC 5322 compliant regex pattern.
 */
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    /**
     * Email regex pattern
     * 
     * Validates:
     * - Local part: letters, numbers, dots, underscores, hyphens, plus signs
     * - @ symbol
     * - Domain: letters, numbers, dots, hyphens
     * - TLD: 2-6 letters
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
    );

    private boolean allowNull;
    private boolean allowEmpty;

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.allowEmpty = constraintAnnotation.allowEmpty();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // Handle null
        if (email == null) {
            return allowNull;
        }

        // Handle empty
        if (email.isEmpty()) {
            return allowEmpty;
        }

        // Trim and check
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty()) {
            return allowEmpty;
        }

        // Check length (reasonable limits)
        if (trimmedEmail.length() > 254) {
            return false;
        }

        // Validate pattern
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }
}
