package com.shopping.microservices.product_service.dto;

import java.io.Serializable;

public record InventoryDTO(
        Long id,
        Long productId,
        String sku,
        Long quantity,
        Long reservedQuantity,
        Long warehouseId
) implements Serializable {
}
