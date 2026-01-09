package com.shopping.microservices.inventory_service.repository;

import com.shopping.microservices.inventory_service.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findBySku(String sku);

    boolean existsByProductId(Long productId);

    boolean existsBySku(String sku);
    Optional<Inventory> findInventoryBySku(String sku);
}

