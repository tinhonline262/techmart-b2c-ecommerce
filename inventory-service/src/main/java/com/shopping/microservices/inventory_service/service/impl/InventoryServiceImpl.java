package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.inventory_service.exception.OutOfStockException;
import com.shopping.microservices.inventory_service.repository.InventoryRepository;
import com.shopping.microservices.inventory_service.service.InventoryService;
import com.shopping.microservices.inventory_service.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService inventoryTransactionService;

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
    public void reserveStock(String sku, Long quantity) {
        log.info("Reserving stock for sku {}", sku);
        inventoryRepository.findInventoryBySku(sku).ifPresent(inventory -> {
            Long currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0L;
            if (currentQuantity < quantity) {
                log.warn("Insufficient stock to reserve for sku {}. Available: {}, Requested: {}",
                    sku, currentQuantity, quantity);
                throw new OutOfStockException("Insufficient stock to reserve for sku " + sku);
            }
            Long updatedQuantity = currentQuantity - quantity;
            inventory.setQuantity(updatedQuantity);

            Long currentReserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;
            Long reservedQuantity = currentReserved + quantity;
            inventory.setReservedQuantity(reservedQuantity);

            inventoryRepository.save(inventory);
            log.info("Stock reserved for sku {}. New available quantity: {}, Reserved: {}",
                sku, updatedQuantity, reservedQuantity);

            // Create transaction history
            inventoryTransactionService.createInventoryTransaction(inventory, -quantity, "Stock reserved for sku: " + sku);
        });
    }

    @Override
    public void releaseStock(String sku, Long quantity) {
        log.info("Releasing stock for sku {}", sku);
        inventoryRepository.findInventoryBySku(sku).ifPresent(inventory -> {
            Long currentReserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0L;
            if (currentReserved < quantity) {
                log.warn("Insufficient reserved stock to release for sku {}. Reserved: {}, Requested: {}",
                    sku, currentReserved, quantity);
                throw new OutOfStockException("Insufficient reserved stock to release for sku " + sku);
            }
            Long updatedReservedQuantity = currentReserved - quantity;
            inventory.setReservedQuantity(updatedReservedQuantity);

            Long currentQuantity = inventory.getQuantity() != null ? inventory.getQuantity() : 0L;
            Long updatedQuantity = currentQuantity + quantity;
            inventory.setQuantity(updatedQuantity);

            inventoryRepository.save(inventory);
            log.info("Stock released for sku {}. New available quantity: {}, Reserved: {}",
                sku, updatedQuantity, updatedReservedQuantity);

            // Create transaction history
            inventoryTransactionService.createInventoryTransaction(inventory, quantity, "Stock released for sku: " + sku);
        });
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
}
