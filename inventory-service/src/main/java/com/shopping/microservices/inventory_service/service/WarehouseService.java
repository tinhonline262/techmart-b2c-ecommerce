package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.inventory_service.entity.Warehouse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface WarehouseService {

    List<Warehouse> findAllWarehouses();

    Optional<Warehouse> findById(Long id);

    Warehouse create(Warehouse warehouse);

    Warehouse update(Long id, Warehouse warehouse);

    void delete(Long id);

    Page<Warehouse> getPageableWarehouses(int pageNo, int pageSize);
    Map<Long, String> getWarehouseNamesByIds(Set<Long> warehouseIds);

}

