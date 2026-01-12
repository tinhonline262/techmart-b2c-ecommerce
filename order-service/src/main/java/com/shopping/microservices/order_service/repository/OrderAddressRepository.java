package com.shopping.microservices.order_service.repository;

import com.shopping.microservices.order_service.entity.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
}