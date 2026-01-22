package com.shopping.microservices.common_library.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validation annotation for minimum amount (BigDecimal).
 * 
 * Validates that a BigDecimal value is greater than or equal to 
 * the specified minimum value.
 * 
 * Example usage:
 * @MinAmount(value = "0", message = "Amount must be non-negative")
 * @MinAmount(value = "0.01", message = "Amount must be at least 0.01")
 * @MinAmount(value = "1000", message = "Minimum order amount is 1000 VND")
 */
@Documented
@Constraint(validatedBy = MinAmountValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAmount {

    /**
     * Error message when validation fails
     */
    String message() default "Amount must be greater than or equal to {value}";

    /**
     * Validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Payload for extensibility
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Minimum value as string (default: "0")
     * Using String to support precise decimal values
     */
    String value() default "0";

    /**
     * Whether to allow null values (default: true)
     */
    boolean allowNull() default true;

    /**
     * Whether the minimum is inclusive (default: true)
     * If true: value >= min
     * If false: value > min
     */
    boolean inclusive() default true;
}
