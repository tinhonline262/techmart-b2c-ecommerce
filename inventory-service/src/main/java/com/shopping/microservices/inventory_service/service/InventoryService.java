package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.inventory_service.entity.Inventory;

import java.util.List;
import java.util.Map;

public interface InventoryService {
    boolean isInStock(String sku, Long quantity);
    boolean isInStock(String sku);
    void updateProductQuantity(Long productId, Long warehouseId, Long adjustedQuantity, String note);
    Map<String, Inventory> findAndLockBySkus(List<String> skus);
    Inventory findAndLockByProductId(Long productId);
    long getAvailableQuantity(Inventory inventory);
    void incrementReservedQuantity(Inventory inventory, int quantity);
    void decrementReservedQuantity(Inventory inventory, long quantity);
    void confirmReservation(Inventory inventory, long reservedQuantity);
}

