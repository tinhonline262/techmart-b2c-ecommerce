package com.shopping.microservices.common_library.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * Validator for @MinAmount annotation.
 * 
 * Validates BigDecimal values against a minimum threshold.
 */
public class MinAmountValidator implements ConstraintValidator<MinAmount, BigDecimal> {

    private BigDecimal minValue;
    private boolean allowNull;
    private boolean inclusive;

    @Override
    public void initialize(MinAmount constraintAnnotation) {
        this.minValue = new BigDecimal(constraintAnnotation.value());
        this.allowNull = constraintAnnotation.allowNull();
        this.inclusive = constraintAnnotation.inclusive();
    }

    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        // Handle null
        if (value == null) {
            return allowNull;
        }

        // Compare with minimum
        int comparison = value.compareTo(minValue);

        if (inclusive) {
            // value >= min
            return comparison >= 0;
        } else {
            // value > min
            return comparison > 0;
        }
    }
}
