package com.shopping.microservices.inventory_service.repository;

import com.shopping.microservices.inventory_service.entity.ReservedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedOrderRepository extends JpaRepository<ReservedOrder, Long> {

    /**
     * Find all reservations for a specific order
     */
    List<ReservedOrder> findByOrderId(String orderId);

    /**
     * Find all reservations for a specific order with a given status
     */
    List<ReservedOrder> findByOrderIdAndStatus(String orderId, ReservedOrder.ReservationStatus status);

    /**
     * Find reservations for a specific product and status
     */
    List<ReservedOrder> findByProductIdAndStatus(Long productId, ReservedOrder.ReservationStatus status);

    /**
     * Find all expired reservations that are still in RESERVED status
     */
    @Query("SELECT r FROM ReservedOrder r WHERE r.status = 'RESERVED' AND r.expiresAt < :currentTime")
    List<ReservedOrder> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Update status for all reservations of an order
     */
    @Modifying
    @Query("UPDATE ReservedOrder r SET r.status = :newStatus, r.updatedAt = :updateTime WHERE r.orderId = :orderId AND r.status = :currentStatus")
    int updateStatusByOrderId(
            @Param("orderId") String orderId,
            @Param("currentStatus") ReservedOrder.ReservationStatus currentStatus,
            @Param("newStatus") ReservedOrder.ReservationStatus newStatus,
            @Param("updateTime") LocalDateTime updateTime
    );

    /**
     * Check if an order has active reservations
     */
    boolean existsByOrderIdAndStatus(String orderId, ReservedOrder.ReservationStatus status);

    /**
     * Get total reserved quantity for a product across all warehouses
     */
    @Query("SELECT COALESCE(SUM(r.reservedQuantity), 0) FROM ReservedOrder r WHERE r.productId = :productId AND r.status = 'RESERVED'")
    Long getTotalReservedQuantityByProduct(@Param("productId") Long productId);

    /**
     * Get total reserved quantity for a product in a specific warehouse
     */
    @Query("SELECT COALESCE(SUM(r.reservedQuantity), 0) FROM ReservedOrder r WHERE r.productId = :productId AND r.warehouseId = :warehouseId AND r.status = 'RESERVED'")
    Long getTotalReservedQuantityByProductAndWarehouse(
            @Param("productId") Long productId,
            @Param("warehouseId") Long warehouseId
    );

    /**
     * Delete old confirmed/cancelled/expired reservations (for cleanup)
     */
    @Modifying
    @Query("DELETE FROM ReservedOrder r WHERE r.status IN ('CANCELLED', 'EXPIRED') AND r.updatedAt < :cutoffDate")
    int deleteOldReservations(@Param("cutoffDate") LocalDateTime cutoffDate);
}

