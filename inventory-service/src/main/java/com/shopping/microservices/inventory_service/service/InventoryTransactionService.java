package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.InventoryTransaction;

import java.util.List;

public interface InventoryTransactionService {

    void createInventoryTransaction(Inventory inventory, Long adjustedQuantity, String note);

    List<InventoryTransaction> getInventoryTransactions(Long productId, Long warehouseId);
}

