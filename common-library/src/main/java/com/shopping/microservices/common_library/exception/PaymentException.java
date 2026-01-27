package com.shopping.microservices.common_library.exception;

import lombok.Getter;

/**
 * Exception thrown when a payment operation fails.
 * 
 * Extends BusinessException with additional payment-specific details
 * including provider error codes for debugging.
 */
@Getter
public class PaymentException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Payment identifier
     */
    private final Long paymentId;

    /**
     * Associated order identifier
     */
    private final Long orderId;

    /**
     * Error code from payment provider
     */
    private final String providerErrorCode;

    /**
     * Payment provider name (e.g., VNPAY, MOMO)
     */
    private final String paymentProvider;

    /**
     * Constructor with payment ID and message
     *
     * @param paymentId Payment identifier
     * @param message Error message
     */
    public PaymentException(Long paymentId, String message) {
        super(message, "PAYMENT_FAILED");
        this.paymentId = paymentId;
        this.orderId = null;
        this.providerErrorCode = null;
        this.paymentProvider = null;
    }

    /**
     * Constructor with payment ID, message, and provider error code
     *
     * @param paymentId Payment identifier
     * @param message Error message
     * @param providerErrorCode Error code from payment provider
     */
    public PaymentException(Long paymentId, String message, String providerErrorCode) {
        super(message, "PAYMENT_FAILED");
        this.paymentId = paymentId;
        this.orderId = null;
        this.providerErrorCode = providerErrorCode;
        this.paymentProvider = null;
    }

    /**
     * Full constructor with all payment details
     *
     * @param paymentId Payment identifier
     * @param orderId Order identifier
     * @param message Error message
     * @param providerErrorCode Error code from payment provider
     * @param paymentProvider Payment provider name
     */
    public PaymentException(Long paymentId, Long orderId, String message, 
                            String providerErrorCode, String paymentProvider) {
        super(message, "PAYMENT_FAILED");
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.providerErrorCode = providerErrorCode;
        this.paymentProvider = paymentProvider;
    }

    /**
     * Constructor with payment ID, message, and cause
     *
     * @param paymentId Payment identifier
     * @param message Error message
     * @param cause Original exception
     */
    public PaymentException(Long paymentId, String message, Throwable cause) {
        super(message, cause, "PAYMENT_FAILED");
        this.paymentId = paymentId;
        this.orderId = null;
        this.providerErrorCode = null;
        this.paymentProvider = null;
    }

    /**
     * Static factory for payment timeout
     */
    public static PaymentException timeout(Long paymentId, Long orderId) {
        return new PaymentException(paymentId, orderId, 
                "Payment timed out", "TIMEOUT", null);
    }

    /**
     * Static factory for payment declined
     */
    public static PaymentException declined(Long paymentId, Long orderId, 
                                            String providerErrorCode, String provider) {
        return new PaymentException(paymentId, orderId, 
                "Payment was declined", providerErrorCode, provider);
    }

    /**
     * Static factory for insufficient funds
     */
    public static PaymentException insufficientFunds(Long paymentId, Long orderId, String provider) {
        return new PaymentException(paymentId, orderId, 
                "Insufficient funds", "INSUFFICIENT_FUNDS", provider);
    }

    /**
     * Static factory for provider unavailable
     */
    public static PaymentException providerUnavailable(Long paymentId, String provider) {
        return new PaymentException(paymentId, null, 
                "Payment provider unavailable: " + provider, "PROVIDER_UNAVAILABLE", provider);
    }
}
