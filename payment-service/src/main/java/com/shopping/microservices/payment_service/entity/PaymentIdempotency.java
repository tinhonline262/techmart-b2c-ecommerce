package com.shopping.microservices.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity for tracking payment callback idempotency.
 * 
 * Prevents duplicate processing of the same callback from payment providers.
 */
@Entity
@Table(name = "payment_idempotency", indexes = {
    @Index(name = "idx_idempotency_key", columnList = "idempotency_key", unique = true),
    @Index(name = "idx_payment_id", columnList = "payment_id"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIdempotency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique idempotency key generated from callback parameters
     * Typically: SHA-256 hash of (providerId + transactionId + amount + timestamp)
     */
    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;

    /**
     * Associated payment ID
     */
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    /**
     * Provider ID (VNPAY, MOMO, PAYPAL, etc.)
     */
    @Column(name = "provider_id", nullable = false, length = 50)
    private String providerId;

    /**
     * Provider's transaction ID
     */
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    /**
     * Hash of the request for verification
     */
    @Column(name = "request_hash", length = 128)
    private String requestHash;

    /**
     * Stored response data (JSON)
     */
    @Column(name = "response_data", columnDefinition = "TEXT")
    private String responseData;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ProcessingStatus status = ProcessingStatus.PROCESSING;

    /**
     * When the record was created
     */
    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * When processing completed
     */
    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    /**
     * Processing status enum
     */
    public enum ProcessingStatus {
        PROCESSING,
        COMPLETED,
        FAILED
    }

    /**
     * Mark as completed
     */
    public void markCompleted(String responseData) {
        this.status = ProcessingStatus.COMPLETED;
        this.responseData = responseData;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Mark as failed
     */
    public void markFailed(String errorMessage) {
        this.status = ProcessingStatus.FAILED;
        this.responseData = errorMessage;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Check if already processed
     */
    public boolean isProcessed() {
        return status == ProcessingStatus.COMPLETED || status == ProcessingStatus.FAILED;
    }
}
