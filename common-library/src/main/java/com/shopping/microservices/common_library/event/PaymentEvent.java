package com.shopping.microservices.common_library.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopping.microservices.common_library.kafka.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Payment Event for publishing payment-related domain events.
 * 
 * Used for communication between Payment Service and other services
 * (Order, Notification).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentEvent extends BaseEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Payment event types
     */
    public enum PaymentEventType {
        PAYMENT_INITIATED,
        PAYMENT_PROCESSING,
        PAYMENT_SUCCESS,
        PAYMENT_FAILED,
        PAYMENT_REFUNDED,
        PAYMENT_TIMEOUT
    }

    /**
     * Unique payment identifier
     */
    private Long paymentId;

    /**
     * Associated order identifier
     */
    private Long orderId;

    /**
     * Order number for reference
     */
    private String orderNumber;

    /**
     * Payment amount
     */
    private BigDecimal amount;

    /**
     * Currency code (default: VND)
     */
    @Builder.Default
    private String currency = "VND";

    /**
     * Payment method (e.g., CREDIT_CARD, BANK_TRANSFER, E_WALLET)
     */
    private String paymentMethod;

    /**
     * Payment provider (e.g., VNPAY, MOMO, ZALOPAY)
     */
    private String paymentProvider;

    /**
     * Transaction ID from payment provider
     */
    private String providerTransactionId;

    /**
     * Reason for payment failure
     */
    private String failureReason;

    /**
     * Customer email for notifications
     */
    private String customerEmail;

    /**
     * Additional metadata for extensibility
     */
    private Map<String, Object> metadata;

    /**
     * Constructor with event type and source
     */
    public PaymentEvent(PaymentEventType eventType, String source) {
        super(eventType.name(), source);
    }

    /**
     * Constructor with event type, source, and correlation ID
     */
    public PaymentEvent(PaymentEventType eventType, String source, String correlationId) {
        super(eventType.name(), source, correlationId);
    }

    /**
     * Static factory method for creating PAYMENT_INITIATED events
     */
    public static PaymentEvent paymentInitiated(String source, Long paymentId, Long orderId, 
                                                BigDecimal amount, String paymentMethod) {
        PaymentEvent event = new PaymentEvent(PaymentEventType.PAYMENT_INITIATED, source);
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setAmount(amount);
        event.setPaymentMethod(paymentMethod);
        return event;
    }

    /**
     * Static factory method for creating PAYMENT_SUCCESS events
     */
    public static PaymentEvent paymentSuccess(String source, Long paymentId, Long orderId, 
                                              BigDecimal amount, String providerTransactionId) {
        PaymentEvent event = new PaymentEvent(PaymentEventType.PAYMENT_SUCCESS, source);
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setAmount(amount);
        event.setProviderTransactionId(providerTransactionId);
        return event;
    }

    /**
     * Static factory method for creating PAYMENT_FAILED events
     */
    public static PaymentEvent paymentFailed(String source, Long paymentId, Long orderId, 
                                             String failureReason) {
        PaymentEvent event = new PaymentEvent(PaymentEventType.PAYMENT_FAILED, source);
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setFailureReason(failureReason);
        return event;
    }

    /**
     * Static factory method for creating PAYMENT_REFUNDED events
     */
    public static PaymentEvent paymentRefunded(String source, Long paymentId, Long orderId, 
                                               BigDecimal amount, String reason) {
        PaymentEvent event = new PaymentEvent(PaymentEventType.PAYMENT_REFUNDED, source);
        event.setPaymentId(paymentId);
        event.setOrderId(orderId);
        event.setAmount(amount);
        event.setFailureReason(reason);
        return event;
    }
}
