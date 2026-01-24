package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.Checkout;
import com.shopping.microservices.order_service.model.enumeration.CheckoutState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CheckoutRepository extends JpaRepository<Checkout, String> {

    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT c FROM Checkout c WHERE c.id = :id")
    Optional<Checkout> findCheckoutWithItems(@Param("id") String id);

    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT c FROM Checkout c WHERE c.id = :id AND c.status = :status")
    Optional<Checkout> findByIdAndCheckoutState(@Param("id") String id, @Param("status") String status);

}