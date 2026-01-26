package com.shopping.microservices.order_service.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthenticationUtils {

    private AuthenticationUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Extracts the user ID from the current security context.
     * The user ID is stored as the principal in the authentication object.
     *
     * @return the authenticated user's ID, or null if not authenticated
     */
    public static String extractUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Checks if there is an authenticated user in the current security context.
     *
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !(authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal()));
    }
}
