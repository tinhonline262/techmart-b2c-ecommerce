package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.CheckoutItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckoutItemRepository extends JpaRepository<CheckoutItem, Long> {
}