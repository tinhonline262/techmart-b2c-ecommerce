package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  }