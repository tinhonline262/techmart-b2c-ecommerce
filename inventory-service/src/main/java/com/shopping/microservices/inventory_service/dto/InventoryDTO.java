package com.shopping.microservices.inventory_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link com.shopping.microservices.inventory_service.entity.Inventory}
 */
public record InventoryDTO(
    Long id,
    @NotNull Long productId,
    @Size(max = 100) String sku,
    Long quantity,
    Long reservedQuantity,
    Long warehouseId
) implements Serializable {
}
