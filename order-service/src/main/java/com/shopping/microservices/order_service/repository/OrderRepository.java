package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.enumeration.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    Page<Order> findByCustomerId(String customerId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);

    Optional<Order> findByCheckoutId(String checkoutId);

    @EntityGraph(attributePaths = {"items", "shippingAddress", "billingAddress"})
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Optional<Order> findOrderWithItems(@Param("orderId") Long orderId);

    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC")
    List<Order> findLatestByCustomerId(@Param("customerId") String customerId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o " +
           "JOIN o.items i WHERE i.productId = :productId AND o.customerId = :customerId AND o.status = :status")
    boolean existsByProductIdAndCustomerIdAndStatus(
            @Param("productId") Long productId,
            @Param("customerId") String customerId,
            @Param("status") OrderStatus status);

    @Query("SELECT o FROM Order o WHERE " +
           "(:startDate IS NULL OR o.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR o.createdAt <= :endDate) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)")
    Page<Order> findOrdersWithFilters(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("status") OrderStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            Pageable pageable);

    List<Order> findTop10ByOrderByCreatedAtDesc();
}