package com.shopping.microservices.inventory_service.config;

import com.shopping.microservices.inventory_service.service.InventoryReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ReservationSchedulerConfig {

    private final InventoryReservationService inventoryReservationService;

    /**
     * Process expired reservations every 5 minutes
     */
    @Scheduled(fixedRate = 30_000) // 5 minutes in milliseconds
    public void processExpiredReservations() {
        log.debug("Running scheduled task: processExpiredReservations");
        inventoryReservationService.processExpiredReservations();
        log.debug("Completed scheduled task: processExpiredReservations");
    }

    /**
     * Clean up old reservations (confirmed/cancelled/expired) every day at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupOldReservations() {
        log.info("Running scheduled task: cleanupOldReservations");
        int deletedCount = inventoryReservationService.cleanupOldReservations(30); // Keep last 30 days
        log.info("Cleanup completed: {} old reservations removed", deletedCount);
    }
}

