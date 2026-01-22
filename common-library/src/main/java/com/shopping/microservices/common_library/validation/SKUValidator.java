package com.shopping.microservices.common_library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Validator for @ValidSKU annotation.
 * 
 * Validates product SKU format.
 */
public class SKUValidator implements ConstraintValidator<ValidSKU, String> {

    /**
     * SKU pattern: uppercase letters, numbers, and hyphens
     */
    private static final Pattern SKU_PATTERN = Pattern.compile(
            "^[A-Z0-9-]+$"
    );

    private boolean allowNull;
    private int minLength;
    private int maxLength;
    private boolean autoUppercase;

    @Override
    public void initialize(ValidSKU constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
        this.minLength = constraintAnnotation.minLength();
        this.maxLength = constraintAnnotation.maxLength();
        this.autoUppercase = constraintAnnotation.autoUppercase();
    }

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        // Handle null
        if (sku == null) {
            return allowNull;
        }

        // Handle empty
        if (sku.isEmpty()) {
            return false;
        }

        // Trim whitespace
        String trimmedSku = sku.trim();

        // Auto uppercase if enabled
        if (autoUppercase) {
            trimmedSku = trimmedSku.toUpperCase();
        }

        // Check length
        if (trimmedSku.length() < minLength || trimmedSku.length() > maxLength) {
            return false;
        }

        // Check pattern
        if (!SKU_PATTERN.matcher(trimmedSku).matches()) {
            return false;
        }

        // Additional validation: cannot start or end with hyphen
        if (trimmedSku.startsWith("-") || trimmedSku.endsWith("-")) {
            return false;
        }

        // Cannot have consecutive hyphens
        if (trimmedSku.contains("--")) {
            return false;
        }

        return true;
    }

    /**
     * Normalize a SKU to standard format (uppercase, trimmed)
     * 
     * @param sku SKU to normalize
     * @return Normalized SKU or null if invalid
     */
    public static String normalize(String sku) {
        if (sku == null || sku.isEmpty()) {
            return null;
        }

        String normalized = sku.trim().toUpperCase();

        // Validate basic pattern
        if (!SKU_PATTERN.matcher(normalized).matches()) {
            return null;
        }

        return normalized;
    }

    /**
     * Generate a SKU from parts
     * 
     * @param parts Parts to join with hyphens
     * @return Generated SKU
     */
    public static String generateSKU(String... parts) {
        if (parts == null || parts.length == 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != null && !parts[i].isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("-");
                }
                sb.append(parts[i].trim().toUpperCase()
                        .replaceAll("[^A-Z0-9]", ""));
            }
        }

        String sku = sb.toString();
        return sku.isEmpty() ? null : sku;
    }
}
