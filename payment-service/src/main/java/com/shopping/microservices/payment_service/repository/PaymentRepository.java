package com.shopping.microservices.payment_service.repository;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByOrderId(Long orderId);
    
    Optional<Payment> findByCheckoutId(String checkoutId);
    
    Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);
    
    Optional<Payment> findByPaymentProviderCheckoutId(String providerCheckoutId);
    
    List<Payment> findByPaymentStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId AND p.paymentStatus = :status")
    Optional<Payment> findByOrderIdAndStatus(
        @Param("orderId") Long orderId,
        @Param("status") PaymentStatus status
    );
    
    @Query("SELECT p FROM Payment p WHERE p.checkoutId = :checkoutId AND p.paymentStatus IN :statuses ORDER BY p.createdAt DESC")
    List<Payment> findByCheckoutIdAndStatusIn(
        @Param("checkoutId") String checkoutId,
        @Param("statuses") List<PaymentStatus> statuses
    );
    
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus IN ('PENDING', 'INITIATED') AND p.createdAt < :before")
    List<Payment> findPendingPaymentsBefore(@Param("before") LocalDateTime before);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentStatus = :status AND p.createdAt >= :since")
    long countByStatusSince(
        @Param("status") PaymentStatus status,
        @Param("since") LocalDateTime since
    );
    
    @Query("SELECT p.paymentMethod, COUNT(p), SUM(p.amount) FROM Payment p WHERE p.paymentStatus = 'SUCCESS' AND p.createdAt BETWEEN :start AND :end GROUP BY p.paymentMethod")
    List<Object[]> getPaymentStatsByMethod(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    boolean existsByOrderIdAndPaymentStatus(Long orderId, PaymentStatus status);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.id = :id")
    Optional<Payment> findByIdWithLock(@Param("id") Long id);

    // Find payment by orderId with pessimistic lock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Payment p WHERE p.orderId = :orderId")
    Optional<Payment> findByOrderIdWithLock(@Param("orderId") Long orderId);
}

