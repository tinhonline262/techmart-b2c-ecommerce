package com.shopping.microservices.inventory_service.repository;

import com.shopping.microservices.inventory_service.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    List<InventoryTransaction> findByProductIdAndWarehouseIdOrderByCreatedAtDesc(Long productId, Long warehouseId);
}
