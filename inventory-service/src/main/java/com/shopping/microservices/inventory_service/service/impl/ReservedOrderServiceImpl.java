package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import com.shopping.microservices.inventory_service.repository.ReservedOrderRepository;
import com.shopping.microservices.inventory_service.service.ReservedOrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservedOrderServiceImpl implements ReservedOrderService {

    private final ReservedOrderRepository reservedOrderRepository;

    @Override
    public ReservedOrder createReservation(Long orderId, OrderEvent.OrderItemData item, Long warehouseId, LocalDateTime expiresAt) {

        ReservedOrder reservation = ReservedOrder.builder()
                .orderId(orderId)
                .productId(item.getProductId())
                .sku(item.getSku())
                .warehouseId(warehouseId)
                .reservedQuantity(item.getQuantity().longValue())
                .status(ReservedOrder.ReservationStatus.RESERVED)
                .expiresAt(expiresAt)
                .build();

        return reservedOrderRepository.save(reservation);
    }

    @Override
    public List<ReservedOrder> findActiveReservations(Long orderId) {
        return reservedOrderRepository.findByOrderIdAndStatus(
                orderId,
                ReservedOrder.ReservationStatus.RESERVED
        );
    }

    @Override
    public void confirmReservation(ReservedOrder reservation) {
        reservation.setStatus(ReservedOrder.ReservationStatus.CONFIRMED);
        reservedOrderRepository.save(reservation);
    }

    @Override
    public void cancelReservation(ReservedOrder reservation) {
        reservation.setStatus(ReservedOrder.ReservationStatus.CANCELLED);
        reservedOrderRepository.save(reservation);
    }

    @Override
    public List<Long> findExpiredOrderIds(LocalDateTime now) {
        return reservedOrderRepository
                .findExpiredReservations(ReservedOrder.ReservationStatus.RESERVED, now)
                .stream()
                .map(ReservedOrder::getOrderId)
                .distinct()
                .toList();
    }
}
