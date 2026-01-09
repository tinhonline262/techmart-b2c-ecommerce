package com.shopping.microservices.inventory_service.dto;

import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservedOrderDTO {

    private Long id;
    private String orderId;
    private Long productId;
    private String sku;
    private Long warehouseId;
    private Long reservedQuantity;
    private ReservedOrder.ReservationStatus status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Request DTO for creating reservation
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservationRequest {
        private String orderId;
        private Long productId;
        private String sku;
        private Long warehouseId;
        private Long quantity;
        private Integer expirationMinutes; // Optional: how many minutes until reservation expires
    }

    // Response DTO for reservation operations
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReservationResponse {
        private boolean success;
        private String message;
        private ReservedOrderDTO reservation;
    }
}

