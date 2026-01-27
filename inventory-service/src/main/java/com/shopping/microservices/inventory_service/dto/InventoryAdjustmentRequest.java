package com.shopping.microservices.inventory_service.dto;

import jakarta.validation.constraints.NotNull;

public record InventoryAdjustmentRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Warehouse ID is required")
        Long warehouseId,

        @NotNull(message = "Adjusted quantity is required")
        Long adjustedQuantity,

        String note
) {
}
