package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutRepository extends JpaRepository<Checkout, String> {
}