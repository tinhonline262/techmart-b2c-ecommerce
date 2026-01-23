package com.shopping.microservices.inventory_service.dto;

import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class ReservationContext {
    private List<ReservationResult> results;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class ReservationResult {
        private ReservedOrder reservation;
        private Inventory inventory;
        private int requestedQuantity;
        private long availableAfterReservation;
    }
}