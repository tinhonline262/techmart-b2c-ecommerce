package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing inventory reservations via Kafka events
 */

public interface ReservedOrderService {

    ReservedOrder createReservation(
            Long orderId,
            OrderEvent.OrderItemData item,
            Long warehouseId,
            LocalDateTime expiresAt);

    List<ReservedOrder> findActiveReservations(Long orderId);

    void confirmReservation(ReservedOrder reservation);

    void cancelReservation(ReservedOrder reservation);

    List<Long> findExpiredOrderIds(LocalDateTime now);
}

