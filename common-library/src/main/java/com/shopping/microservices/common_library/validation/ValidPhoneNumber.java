package com.shopping.microservices.common_library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for Vietnamese phone numbers.
 * 
 * Validates phone numbers in Vietnamese format:
 * - Starts with 0 or +84
 * - Followed by 9-10 digits
 * 
 * Example valid formats:
 * - 0912345678
 * - 0123456789
 * - +84912345678
 * - +841234567890
 */
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {

    /**
     * Error message when validation fails
     */
    String message() default "Invalid phone number format. Must be Vietnamese format: 0xxxxxxxxx or +84xxxxxxxxx";

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
     * Whether to allow spaces and dashes in the number (default: true)
     */
    boolean allowFormatting() default true;
}
