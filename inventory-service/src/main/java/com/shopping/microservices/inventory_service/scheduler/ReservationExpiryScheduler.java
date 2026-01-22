package com.shopping.microservices.inventory_service.scheduler;

import com.shopping.microservices.inventory_service.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationExpiryScheduler {

    private final ReservationService reservationService;

    // Run every 5 minutes to check for expired reservations
    @Scheduled(fixedRate = 300000)
    public void processExpiredReservations() {
        log.info("Starting scheduled task: process expired reservations");
        try {
            reservationService.processExpiredReservations();
            log.info("Completed scheduled task: process expired reservations");
        } catch (Exception e) {
            log.error("Error in scheduled task for expired reservations: {}", e.getMessage(), e);
        }
    }
}
