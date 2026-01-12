package com.shopping.microservices.cart_service.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for security-related operations
 */
public class SecurityUtils {

    private SecurityUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Retrieves the current authenticated customer ID from the security context
     *
     * @return the customer ID from JWT token
     * @throws IllegalStateException if no authentication is present
     */
    public static String getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        // Extract customerId from JWT principal/claims
        // Assuming the principal name contains the customerId
        return authentication.getName();
    }
}
