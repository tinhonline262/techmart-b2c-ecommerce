package com.shopping.microservices.common_library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for Product SKU (Stock Keeping Unit).
 * 
 * Validates SKU format:
 * - 6-20 characters
 * - Uppercase letters, numbers, and hyphens only
 * 
 * Example valid SKUs:
 * - ABC-123
 * - PROD-001-XL
 * - SKU12345
 * - LAPTOP-DELL-15
 */
@Documented
@Constraint(validatedBy = SKUValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSKU {

    /**
     * Error message when validation fails
     */
    String message() default "Invalid SKU format. Must be 6-20 characters, uppercase letters, numbers, and hyphens only";

    /**
     * Validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for extensibility
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Whether to allow null values (default: true)
     */
    boolean allowNull() default true;

    /**
     * Minimum length (default: 6)
     */
    int minLength() default 6;

    /**
     * Maximum length (default: 20)
     */
    int maxLength() default 20;

    /**
     * Whether to auto-convert to uppercase during validation (default: true)
     */
    boolean autoUppercase() default true;
}
