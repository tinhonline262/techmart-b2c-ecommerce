package com.shopping.microservices.inventory_service.mapper;

import com.shopping.microservices.inventory_service.dto.InventoryDTO;
import com.shopping.microservices.inventory_service.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryDTO toDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        return new InventoryDTO(
            inventory.getId(),
            inventory.getProductId(),
            inventory.getSku(),
            inventory.getQuantity(),
            inventory.getReservedQuantity(),
            inventory.getWarehouseId()
        );
    }

    public Inventory toEntity(InventoryDTO dto) {
        if (dto == null) {
            return null;
        }

        return Inventory.builder()
            .id(dto.id())
            .productId(dto.productId())
            .sku(dto.sku())
            .quantity(dto.quantity())
            .reservedQuantity(dto.reservedQuantity())
            .build();
    }
}

