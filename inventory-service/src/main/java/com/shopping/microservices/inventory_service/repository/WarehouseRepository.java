package com.shopping.microservices.inventory_service.repository;

import com.shopping.microservices.inventory_service.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    boolean existsByName(String name);

    @Query("SELECT CASE WHEN COUNT(w) > 0 THEN true ELSE false END FROM Warehouse w WHERE w.name = :name AND w.id <> :id")
    boolean existsByNameWithDifferentId(String name, Long id);
}

