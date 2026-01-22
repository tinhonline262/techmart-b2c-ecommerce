package com.shopping.microservices.common_library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for email addresses.
 * 
 * Uses a comprehensive regex pattern that validates:
 * - Local part (before @)
 * - Domain part (after @)
 * - Top-level domain (2-6 characters)
 * 
 * Example valid emails:
 * - user@example.com
 * - user.name@example.co.uk
 * - user+tag@example.org
 */
@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {

    /**
     * Error message when validation fails
     */
    String message() default "Invalid email format";

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
     * Whether to allow empty strings (default: false)
     */
    boolean allowEmpty() default false;
}
