package com.shopping.microservices.payment_service.service;

import com.shopping.microservices.common_library.utils.IdempotencyUtil;
import com.shopping.microservices.payment_service.entity.PaymentIdempotency;
import com.shopping.microservices.payment_service.repository.PaymentIdempotencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Service for managing payment callback idempotency.
 * 
 * Ensures callbacks from payment providers are only processed once.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentIdempotencyService {

    private final PaymentIdempotencyRepository idempotencyRepository;

    /**
     * Generate idempotency key from callback parameters
     */
    public String generateIdempotencyKey(String providerId, Map<String, String> params) {
        // Extract key parameters based on provider
        String transactionId = extractTransactionId(providerId, params);
        String amount = extractAmount(providerId, params);
        String timestamp = extractTimestamp(providerId, params);
        
        return IdempotencyUtil.generateIdempotencyKey(providerId, transactionId, amount, timestamp);
    }

    /**
     * Check if callback has already been processed
     */
    @Transactional(readOnly = true)
    public Optional<PaymentIdempotency> findByKey(String idempotencyKey) {
        return idempotencyRepository.findByIdempotencyKey(idempotencyKey);
    }

    /**
     * Check if callback is duplicate
     */
    @Transactional(readOnly = true)
    public boolean isDuplicate(String idempotencyKey) {
        Optional<PaymentIdempotency> existing = findByKey(idempotencyKey);
        return existing.isPresent() && existing.get().isProcessed();
    }

    /**
     * Start processing a callback (create idempotency record)
     * 
     * @return true if processing can proceed, false if already being processed
     */
    @Transactional
    public boolean startProcessing(String idempotencyKey, Long paymentId, String providerId, 
                                   String transactionId, String requestHash) {
        // Check if already exists
        if (idempotencyRepository.existsByIdempotencyKey(idempotencyKey)) {
            log.info("Idempotency key already exists, skipping: {}", idempotencyKey);
            return false;
        }

        // Create new record
        PaymentIdempotency record = PaymentIdempotency.builder()
            .idempotencyKey(idempotencyKey)
            .paymentId(paymentId)
            .providerId(providerId)
            .transactionId(transactionId)
            .requestHash(requestHash)
            .status(PaymentIdempotency.ProcessingStatus.PROCESSING)
            .build();

        try {
            idempotencyRepository.save(record);
            log.debug("Created idempotency record: {}", idempotencyKey);
            return true;
        } catch (Exception e) {
            // Likely duplicate key violation due to race condition
            log.info("Failed to create idempotency record (likely duplicate): {}", idempotencyKey);
            return false;
        }
    }

    /**
     * Mark callback processing as completed
     */
    @Transactional
    public void markCompleted(String idempotencyKey, String responseData) {
        idempotencyRepository.findByIdempotencyKey(idempotencyKey)
            .ifPresent(record -> {
                record.markCompleted(responseData);
                idempotencyRepository.save(record);
                log.debug("Marked idempotency record as completed: {}", idempotencyKey);
            });
    }

    /**
     * Mark callback processing as failed
     */
    @Transactional
    public void markFailed(String idempotencyKey, String errorMessage) {
        idempotencyRepository.findByIdempotencyKey(idempotencyKey)
            .ifPresent(record -> {
                record.markFailed(errorMessage);
                idempotencyRepository.save(record);
                log.debug("Marked idempotency record as failed: {}", idempotencyKey);
            });
    }

    /**
     * Get stored response for duplicate callback
     */
    @Transactional(readOnly = true)
    public Optional<String> getStoredResponse(String idempotencyKey) {
        return idempotencyRepository.findByIdempotencyKey(idempotencyKey)
            .filter(PaymentIdempotency::isProcessed)
            .map(PaymentIdempotency::getResponseData);
    }

    // ==================== Helper Methods ====================

    private String extractTransactionId(String providerId, Map<String, String> params) {
        return switch (providerId) {
            case "VNPAY" -> params.getOrDefault("vnp_TransactionNo", "");
            case "MOMO" -> params.getOrDefault("transId", "");
            case "PAYPAL" -> params.getOrDefault("capture_id", params.getOrDefault("resource_id", ""));
            case "COD" -> params.getOrDefault("confirmationCode", "");
            default -> params.getOrDefault("transactionId", "");
        };
    }

    private String extractAmount(String providerId, Map<String, String> params) {
        return switch (providerId) {
            case "VNPAY" -> params.getOrDefault("vnp_Amount", "");
            case "MOMO" -> params.getOrDefault("amount", "");
            case "PAYPAL" -> params.getOrDefault("amount", "");
            case "COD" -> params.getOrDefault("collectedAmount", "");
            default -> params.getOrDefault("amount", "");
        };
    }

    private String extractTimestamp(String providerId, Map<String, String> params) {
        return switch (providerId) {
            case "VNPAY" -> params.getOrDefault("vnp_PayDate", "");
            case "MOMO" -> params.getOrDefault("responseTime", "");
            case "PAYPAL" -> params.getOrDefault("create_time", "");
            case "COD" -> params.getOrDefault("collectedAt", "");
            default -> params.getOrDefault("timestamp", String.valueOf(System.currentTimeMillis()));
        };
    }
}
