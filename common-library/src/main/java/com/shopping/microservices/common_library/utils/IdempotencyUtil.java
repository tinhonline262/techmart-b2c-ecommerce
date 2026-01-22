package com.shopping.microservices.common_library.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for idempotency key generation and validation.
 * 
 * Provides methods to generate deterministic idempotency keys
 * for ensuring exactly-once processing of requests.
 * 
 * Use cases:
 * - Payment processing (prevent double charges)
 * - Order creation (prevent duplicate orders)
 * - API request deduplication
 */
public final class IdempotencyUtil {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyUtil.class);

    /**
     * Pattern for validating idempotency keys (Base64 URL-safe characters)
     */
    private static final Pattern IDEMPOTENCY_KEY_PATTERN = 
            Pattern.compile("^[A-Za-z0-9_-]{32,64}$");

    /**
     * Separator used when concatenating parts
     */
    private static final String SEPARATOR = ":";

    /**
     * SHA-256 algorithm name
     */
    private static final String SHA_256 = "SHA-256";

    // Private constructor to prevent instantiation
    private IdempotencyUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Generate an idempotency key from multiple parts using SHA-256.
     * 
     * The key is deterministic - the same input parts will always
     * produce the same key.
     * 
     * Example:
     * generateIdempotencyKey("ORDER", "user123", "2024-01-15", "12345")
     * 
     * @param parts Parts to hash
     * @return Base64 URL-safe encoded SHA-256 hash
     */
    public static String generateIdempotencyKey(String... parts) {
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("At least one part is required");
        }

        // Filter null values and join with separator
        String combined = Arrays.stream(parts)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(SEPARATOR));

        if (combined.isEmpty()) {
            throw new IllegalArgumentException("All parts were null or empty");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 should always be available
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Generate an idempotency key for a payment.
     * 
     * @param orderId Order ID
     * @param amount Payment amount
     * @param timestamp Timestamp (ISO format recommended)
     * @return Idempotency key
     */
    public static String generatePaymentKey(Long orderId, String amount, String timestamp) {
        return generateIdempotencyKey("PAYMENT", 
                String.valueOf(orderId), 
                amount, 
                timestamp);
    }

    /**
     * Generate an idempotency key for an order.
     * 
     * @param customerId Customer ID
     * @param cartHash Hash of cart contents
     * @param timestamp Timestamp
     * @return Idempotency key
     */
    public static String generateOrderKey(String customerId, String cartHash, String timestamp) {
        return generateIdempotencyKey("ORDER", customerId, cartHash, timestamp);
    }

    /**
     * Generate an idempotency key for an inventory reservation.
     * 
     * @param orderId Order ID
     * @param productId Product ID
     * @return Idempotency key
     */
    public static String generateReservationKey(Long orderId, Long productId) {
        return generateIdempotencyKey("RESERVATION", 
                String.valueOf(orderId), 
                String.valueOf(productId));
    }

    /**
     * Generate a random idempotency key (non-deterministic).
     * 
     * Use this when you don't need deterministic keys,
     * just unique identifiers.
     * 
     * @return Random Base64 URL-safe key
     */
    public static String generateRandomKey() {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validate an idempotency key format.
     * 
     * @param key Key to validate
     * @return true if valid format, false otherwise
     */
    public static boolean isValidIdempotencyKey(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        }
        return IDEMPOTENCY_KEY_PATTERN.matcher(key).matches();
    }

    /**
     * Generate a correlation ID for distributed tracing.
     * 
     * Similar to idempotency keys but designed for tracking
     * requests across services.
     * 
     * @return UUID-based correlation ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generate a correlation ID with a prefix.
     * 
     * @param prefix Prefix for the correlation ID (e.g., "ORD", "PAY")
     * @return Prefixed correlation ID
     */
    public static String generateCorrelationId(String prefix) {
        if (prefix == null || prefix.isEmpty()) {
            return generateCorrelationId();
        }
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Extract prefix from a correlation ID.
     * 
     * @param correlationId Correlation ID
     * @return Prefix or null if no prefix
     */
    public static String extractPrefix(String correlationId) {
        if (correlationId == null || !correlationId.contains("-")) {
            return null;
        }
        int firstDash = correlationId.indexOf('-');
        String prefix = correlationId.substring(0, firstDash);
        // Check if it looks like a prefix (short, uppercase)
        if (prefix.length() <= 5 && prefix.equals(prefix.toUpperCase())) {
            return prefix;
        }
        return null;
    }

    /**
     * Hash a string using SHA-256.
     * 
     * @param input String to hash
     * @return Hex-encoded hash
     */
    public static String sha256Hex(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(SHA_256);
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
