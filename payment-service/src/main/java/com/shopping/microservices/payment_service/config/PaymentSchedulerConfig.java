package com.shopping.microservices.payment_service.config;

import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Scheduler configuration for payment-related background tasks.
 * 
 * Tasks:
 * - Expire pending payments that have exceeded timeout
 */
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class PaymentSchedulerConfig {

    private final PaymentService paymentService;

    /**
     * Expire pending payments every 5 minutes.
     * Payments in PENDING or INITIATED status older than configured timeout will be marked as EXPIRED.
     */
    @Scheduled(fixedRate = 300_000) // Every 5 minutes
    public void expirePendingPayments() {
        log.debug("Running payment expiry job");
        try {
            int expiredCount = paymentService.expirePendingPayments();
            if (expiredCount > 0) {
                log.info("Payment expiry job completed: {} payments expired", expiredCount);
            }
        } catch (Exception e) {
            log.error("Error in payment expiry job", e);
        }
    }
}
