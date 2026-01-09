package com.shopping.microservices.inventory_service.service;

public interface InventoryService {
    boolean isInStock(String sku, Long quantity);
    boolean isInStock(String sku);
    void reserveStock(String sku, Long quantity);
    void releaseStock(String sku, Long quantity);
    void updateProductQuantity(Long productId, Long warehouseId, Long adjustedQuantity, String note);
}

