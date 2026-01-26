package com.shopping.microservices.payment_service.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Get from security context if available
        // For now, return system user
        try {
            // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // if (authentication != null && authentication.isAuthenticated()) {
            //     return Optional.of(authentication.getName());
            // }
            return Optional.of("SYSTEM");
        } catch (Exception e) {
            return Optional.of("SYSTEM");
        }
    }
}
