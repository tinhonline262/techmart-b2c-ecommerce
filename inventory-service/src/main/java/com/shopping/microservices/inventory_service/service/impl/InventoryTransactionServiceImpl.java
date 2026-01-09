package com.shopping.microservices.inventory_service.service.impl;

import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.entity.InventoryTransaction;
import com.shopping.microservices.inventory_service.repository.InventoryTransactionRepository;
import com.shopping.microservices.inventory_service.repository.WarehouseRepository;
import com.shopping.microservices.inventory_service.service.InventoryTransactionService;
import com.shopping.microservices.inventory_service.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    public void createInventoryTransaction(Inventory inventory, Long adjustedQuantity, String note) {
        warehouseRepository.findById(inventory.getWarehouseId()).ifPresent(warehouse -> {
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .productId(inventory.getProductId())
                    .adjustedQuantity(adjustedQuantity)
                    .note(note)
                    .warehouse(warehouse)
                    .build();

            inventoryTransactionRepository.save(transaction);
            log.debug("Inventory transaction created for productId: {}, adjusted quantity: {}",
                    inventory.getProductId(), adjustedQuantity);
        });
    }

    @Override
    public List<InventoryTransaction> getInventoryTransactions(Long productId, Long warehouseId) {
        return inventoryTransactionRepository.findByProductIdAndWarehouseIdOrderByCreatedAtDesc(
            productId, warehouseId
        );
    }
}

