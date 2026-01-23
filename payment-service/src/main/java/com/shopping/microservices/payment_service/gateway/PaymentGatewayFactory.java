package com.shopping.microservices.payment_service.gateway;

import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.repository.PaymentProviderRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Factory for selecting and managing payment gateways.
 * 
 * Features:
 * - Auto-injection of all PaymentGateway beans
 * - Map-based gateway registry indexed by provider ID
 * - Runtime gateway enable/disable support
 * - Payment method to gateway mapping
 * - Thread-safe implementation
 */
@Component
@Slf4j
public class PaymentGatewayFactory {

    private final Map<String, PaymentGateway> gatewayRegistry = new ConcurrentHashMap<>();
    private final Map<PaymentMethod, List<PaymentGateway>> methodToGatewayMap = new ConcurrentHashMap<>();
    private final PaymentProviderRepository providerRepository;

    /**
     * Constructor auto-injects all PaymentGateway beans
     */
    public PaymentGatewayFactory(List<PaymentGateway> gateways, PaymentProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
        registerGateways(gateways);
    }

    @PostConstruct
    public void init() {
        log.info("PaymentGatewayFactory initialized with {} gateways: {}", 
            gatewayRegistry.size(), gatewayRegistry.keySet());
        
        // Log supported methods for each gateway
        gatewayRegistry.forEach((providerId, gateway) -> {
            log.info("Gateway '{}' supports methods: {}", providerId, gateway.getSupportedMethods());
        });
    }

    /**
     * Register all gateways and build method mappings
     */
    private void registerGateways(List<PaymentGateway> gateways) {
        // Check for duplicate provider IDs
        Map<String, Long> idCounts = gateways.stream()
            .collect(Collectors.groupingBy(PaymentGateway::getProviderId, Collectors.counting()));
        
        idCounts.entrySet().stream()
            .filter(e -> e.getValue() > 1)
            .findFirst()
            .ifPresent(e -> {
                throw new IllegalStateException("Duplicate payment gateway provider ID: " + e.getKey());
            });

        // Register gateways
        for (PaymentGateway gateway : gateways) {
            String providerId = gateway.getProviderId();
            gatewayRegistry.put(providerId, gateway);
            
            // Build method to gateway mapping
            for (PaymentMethod method : gateway.getSupportedMethods()) {
                methodToGatewayMap.computeIfAbsent(method, k -> new ArrayList<>()).add(gateway);
            }
        }
    }

    /**
     * Get gateway by provider ID
     * 
     * @param providerId The provider identifier (e.g., "VNPAY", "MOMO")
     * @return The payment gateway
     * @throws GatewayNotFoundException if gateway not found or disabled
     */
    public PaymentGateway getGatewayById(String providerId) {
        PaymentGateway gateway = gatewayRegistry.get(providerId);
        
        if (gateway == null) {
            throw new GatewayNotFoundException("Payment gateway not found: " + providerId);
        }

        // Check if gateway is enabled in database
        if (!isGatewayEnabled(providerId)) {
            throw new GatewayNotFoundException("Payment gateway is disabled: " + providerId);
        }

        return gateway;
    }

    /**
     * Get gateway by provider ID without checking if enabled
     * 
     * @param providerId The provider identifier
     * @return Optional containing the gateway if found
     */
    public Optional<PaymentGateway> findGatewayById(String providerId) {
        return Optional.ofNullable(gatewayRegistry.get(providerId));
    }

    /**
     * Get first available gateway supporting the given payment method
     * 
     * @param method The payment method
     * @return The payment gateway
     * @throws UnsupportedPaymentMethodException if no gateway supports the method
     */
    public PaymentGateway getGatewayByMethod(PaymentMethod method) {
        List<PaymentGateway> gateways = methodToGatewayMap.get(method);
        
        if (gateways == null || gateways.isEmpty()) {
            throw new UnsupportedPaymentMethodException("No gateway supports payment method: " + method);
        }

        // Return first enabled gateway
        for (PaymentGateway gateway : gateways) {
            if (isGatewayEnabled(gateway.getProviderId())) {
                return gateway;
            }
        }

        throw new UnsupportedPaymentMethodException(
            "No enabled gateway supports payment method: " + method);
    }

    /**
     * Get all gateways supporting the given payment method
     * 
     * @param method The payment method
     * @return List of supporting gateways (may be empty)
     */
    public List<PaymentGateway> getGatewaysByMethod(PaymentMethod method) {
        List<PaymentGateway> gateways = methodToGatewayMap.getOrDefault(method, Collections.emptyList());
        return gateways.stream()
            .filter(g -> isGatewayEnabled(g.getProviderId()))
            .collect(Collectors.toList());
    }

    /**
     * Get all registered gateways
     * 
     * @return Collection of all gateways
     */
    public Collection<PaymentGateway> getAllGateways() {
        return Collections.unmodifiableCollection(gatewayRegistry.values());
    }

    /**
     * Get all enabled gateways
     * 
     * @return List of enabled gateways
     */
    public List<PaymentGateway> getEnabledGateways() {
        return gatewayRegistry.values().stream()
            .filter(g -> isGatewayEnabled(g.getProviderId()))
            .collect(Collectors.toList());
    }

    /**
     * Get all supported payment methods across all enabled gateways
     * 
     * @return Set of supported payment methods
     */
    public Set<PaymentMethod> getSupportedMethods() {
        return gatewayRegistry.values().stream()
            .filter(g -> isGatewayEnabled(g.getProviderId()))
            .flatMap(g -> g.getSupportedMethods().stream())
            .collect(Collectors.toSet());
    }

    /**
     * Check if a payment method is supported by any enabled gateway
     * 
     * @param method The payment method to check
     * @return true if supported
     */
    public boolean isMethodSupported(PaymentMethod method) {
        return methodToGatewayMap.containsKey(method) &&
            methodToGatewayMap.get(method).stream()
                .anyMatch(g -> isGatewayEnabled(g.getProviderId()));
    }

    /**
     * Check if a gateway is enabled
     * 
     * @param providerId The provider identifier
     * @return true if enabled
     */
    public boolean isGatewayEnabled(String providerId) {
        return providerRepository.existsByIdAndEnabled(providerId, true);
    }

    /**
     * Get gateway with fallback support
     * 
     * @param primaryProviderId Primary gateway provider ID
     * @param fallbackProviderId Fallback gateway provider ID
     * @return The primary gateway if available, otherwise fallback
     */
    public PaymentGateway getGatewayWithFallback(String primaryProviderId, String fallbackProviderId) {
        try {
            return getGatewayById(primaryProviderId);
        } catch (GatewayNotFoundException e) {
            log.warn("Primary gateway '{}' not available, trying fallback '{}'", 
                primaryProviderId, fallbackProviderId);
            return getGatewayById(fallbackProviderId);
        }
    }

    /**
     * Map payment method to provider ID
     * Returns the default/primary provider for a payment method
     * 
     * @param method The payment method
     * @return The provider ID
     */
    public String getProviderIdForMethod(PaymentMethod method) {
        return switch (method) {
            case VNPAY, BANK_TRANSFER, ATM_CARD -> "VNPAY";
            case MOMO, WALLET -> "MOMO";
            case PAYPAL -> "PAYPAL";
            case COD -> "COD";
            case CREDIT_CARD -> {
                // Credit card could be handled by multiple providers
                // Return first available
                if (isGatewayEnabled("PAYPAL")) yield "PAYPAL";
                if (isGatewayEnabled("VNPAY")) yield "VNPAY";
                if (isGatewayEnabled("MOMO")) yield "MOMO";
                yield "VNPAY"; // Default
            }
        };
    }

    // ==================== Exception Classes ====================

    public static class GatewayNotFoundException extends RuntimeException {
        public GatewayNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnsupportedPaymentMethodException extends RuntimeException {
        public UnsupportedPaymentMethodException(String message) {
            super(message);
        }
    }
}
