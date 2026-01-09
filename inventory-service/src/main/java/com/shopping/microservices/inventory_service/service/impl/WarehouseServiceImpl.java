package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.inventory_service.entity.Warehouse;
import com.shopping.microservices.inventory_service.repository.WarehouseRepository;
import com.shopping.microservices.inventory_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Warehouse> findAllWarehouses() {
        log.debug("Finding all warehouses");
        return warehouseRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Warehouse> findById(Long id) {
        log.debug("Finding warehouse by id: {}", id);
        return warehouseRepository.findById(id);
    }

    @Override
    public Warehouse create(Warehouse warehouse) {
        log.info("Creating warehouse: {}", warehouse.getName());

        if (warehouseRepository.existsByName(warehouse.getName())) {
            log.warn("Warehouse with name '{}' already exists", warehouse.getName());
            throw new IllegalArgumentException("Warehouse with name '" + warehouse.getName() + "' already exists");
        }

        Warehouse savedWarehouse = warehouseRepository.save(warehouse);
        log.info("Warehouse created with id: {}", savedWarehouse.getId());
        return savedWarehouse;
    }

    @Override
    public Warehouse update(Long id, Warehouse warehouse) {
        log.info("Updating warehouse with id: {}", id);

        Warehouse existingWarehouse = warehouseRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Warehouse not found with id: {}", id);
                return new IllegalArgumentException("Warehouse not found with id: " + id);
            });

        if (warehouseRepository.existsByNameWithDifferentId(warehouse.getName(), id)) {
            log.warn("Warehouse with name '{}' already exists", warehouse.getName());
            throw new IllegalArgumentException("Warehouse with name '" + warehouse.getName() + "' already exists");
        }

        existingWarehouse.setName(warehouse.getName());
        existingWarehouse.setAddressId(warehouse.getAddressId());

        Warehouse updatedWarehouse = warehouseRepository.save(existingWarehouse);
        log.info("Warehouse updated successfully: {}", updatedWarehouse.getId());
        return updatedWarehouse;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting warehouse with id: {}", id);

        if (!warehouseRepository.existsById(id)) {
            log.warn("Warehouse not found with id: {}", id);
            throw new IllegalArgumentException("Warehouse not found with id: " + id);
        }

        warehouseRepository.deleteById(id);
        log.info("Warehouse deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Warehouse> getPageableWarehouses(int pageNo, int pageSize) {
        log.debug("Getting pageable warehouses - page: {}, size: {}", pageNo, pageSize);
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return warehouseRepository.findAll(pageable);
    }
}

