package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
  }