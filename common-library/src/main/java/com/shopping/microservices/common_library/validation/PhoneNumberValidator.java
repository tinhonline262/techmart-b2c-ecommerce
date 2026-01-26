package com.shopping.microservices.common_library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for @ValidPhoneNumber annotation.
 * 
 * Validates Vietnamese phone numbers.
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    /**
     * Vietnamese phone number pattern
     * 
     * Accepts:
     * - 0 followed by 9-10 digits
     * - +84 followed by 9-10 digits
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(0|\\+84)[0-9]{9,10}$"
    );

    private boolean allowNull;
    private boolean allowFormatting;

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.allowFormatting = constraintAnnotation.allowFormatting();
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        // Handle null
        if (phoneNumber == null) {
            return allowNull;
        }

        // Handle empty
        if (phoneNumber.isEmpty()) {
            return false;
        }

        // Clean the phone number
        String cleanedPhone = phoneNumber;
        
        if (allowFormatting) {
            // Remove spaces, dashes, dots, and parentheses
            cleanedPhone = phoneNumber.replaceAll("[\\s\\-\\.\\(\\)]", "");
        }

        // Validate pattern
        return PHONE_PATTERN.matcher(cleanedPhone).matches();
    }

    /**
     * Normalize a phone number to standard format (0xxxxxxxxx)
     * 
     * @param phoneNumber Phone number to normalize
     * @return Normalized phone number or null if invalid
     */
    public static String normalize(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return null;
        }

        // Remove formatting
        String cleaned = phoneNumber.replaceAll("[\\s\\-\\.\\(\\)]", "");

        // Convert +84 to 0
        if (cleaned.startsWith("+84")) {
            cleaned = "0" + cleaned.substring(3);
        }

        // Validate
        if (!PHONE_PATTERN.matcher(cleaned.startsWith("0") ? cleaned : "0" + cleaned).matches()) {
            return null;
        }

        return cleaned;
    }
}
