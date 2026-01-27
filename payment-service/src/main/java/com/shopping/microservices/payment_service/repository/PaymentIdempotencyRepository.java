package com.shopping.microservices.payment_service.repository;

import com.shopping.microservices.payment_service.entity.PaymentIdempotency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for payment idempotency tracking
 */
@Repository
public interface PaymentIdempotencyRepository extends JpaRepository<PaymentIdempotency, Long> {

    /**
     * Find by idempotency key
     */
    Optional<PaymentIdempotency> findByIdempotencyKey(String idempotencyKey);

    /**
     * Check if idempotency key exists
     */
    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Find by payment ID and provider
     */
    Optional<PaymentIdempotency> findByPaymentIdAndProviderId(Long paymentId, String providerId);

    /**
     * Find by transaction ID and provider
     */
    Optional<PaymentIdempotency> findByTransactionIdAndProviderId(String transactionId, String providerId);

    /**
     * Delete old records (cleanup job)
     */
    @Modifying
    @Query("DELETE FROM PaymentIdempotency p WHERE p.createdAt < :before")
    int deleteOldRecords(@Param("before") LocalDateTime before);

    /**
     * Count records by status
     */
    long countByStatus(PaymentIdempotency.ProcessingStatus status);
}
