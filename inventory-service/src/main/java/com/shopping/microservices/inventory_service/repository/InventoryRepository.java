package com.shopping.microservices.inventory_service.repository;

import com.shopping.microservices.inventory_service.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Pessimistic lock for concurrent reservation handling
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdWithLock(@Param("productId") Long productId);

    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findBySku(String sku);

    // Pessimistic lock by SKU
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.sku = :sku")
    Optional<Inventory> findBySkuWithLock(@Param("sku") String sku);

    // Find items where available quantity (quantity - reservedQuantity) is below threshold
    @Query("SELECT i FROM Inventory i WHERE (i.quantity - i.reservedQuantity) < :threshold")
    List<Inventory> findLowStockItems(@Param("threshold") Long threshold);

    boolean existsByProductId(Long productId);

    boolean existsBySku(String sku);
    
    Optional<Inventory> findInventoryBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.sku IN :skus")
    List<Inventory> findBySkuInWithLock(@Param("skus") List<String> skus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventory i WHERE i.productId = :productId")
    Optional<Inventory> findByProductIdWithLock(@Param("productId") String productId);

}

