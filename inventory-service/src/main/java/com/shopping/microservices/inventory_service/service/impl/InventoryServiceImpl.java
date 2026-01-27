package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.common_library.event.InventoryEvent;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.exception.OutOfStockException;
import com.shopping.microservices.inventory_service.exception.ProductNotFoundException;
import com.shopping.microservices.inventory_service.repository.InventoryRepository;
import com.shopping.microservices.inventory_service.service.InventoryService;
import com.shopping.microservices.inventory_service.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;
    private final EventPublisher eventPublisher;

    @Override
    public boolean isInStock(String sku, Long quantity) {
        return inventoryRepository.findInventoryBySku(sku)
                .map(inventory -> inventory.getQuantity() != null && inventory.getQuantity() >= quantity)
                .orElse(false);
    }

    @Override
    public boolean isInStock(String sku) {
        return inventoryRepository.findInventoryBySku(sku)
                .map(inventory -> inventory.getQuantity() != null && inventory.getQuantity() > 0)
                .orElse(false);
    }

    @Override
    public void updateProductQuantity(Long productId, Long warehouseId, Long adjustedQuantity, String note) {
        log.info("Updating product quantity for productId: {}, warehouseId: {}", productId, warehouseId);

        inventoryRepository.findByProductId(productId).ifPresent(inventory -> {
            if (inventory.getWarehouseId().equals(warehouseId)) {
                Long currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0L;

                if (adjustedQuantity < 0 && Math.abs(adjustedQuantity) > currentQuantity) {
                    throw new OutOfStockException("Cannot reduce stock below zero for productId: " + productId);
                }

                Long newQuantity = currentQuantity + adjustedQuantity;
                inventory.setQuantity(newQuantity);
                inventoryRepository.save(inventory);

                log.info("Product quantity updated for productId: {}. New quantity: {}", productId, newQuantity);

                // Create transaction history
                inventoryTransactionService.createInventoryTransaction(inventory, adjustedQuantity, note);
            }
        });
    }

    @Override
    public Map<String, Inventory> findAndLockBySkus(List<String> skus) {
        return inventoryRepository.findBySkuInWithLock(skus).stream()
                .collect(Collectors.toMap(Inventory::getSku, inv -> inv));
    }

    @Override
    public Inventory findAndLockByProductId(Long productId) {
        return inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + productId));
    }

    @Override
    public long getAvailableQuantity(Inventory inventory) {
        return inventory.getQuantity() - inventory.getReservedQuantity();
    }

    @Override
    public void incrementReservedQuantity(Inventory inventory, int quantity) {
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void decrementReservedQuantity(Inventory inventory, long quantity) {
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    public void confirmReservation(Inventory inventory, long reservedQuantity) {
        inventory.setQuantity(inventory.getQuantity() - reservedQuantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservedQuantity);
        inventoryRepository.save(inventory);
    }
}
