package com.shopping.microservices.product_service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record WarehouseProductDTO(
        Long id,
        String name,
        String sku,
        Integer stockQuantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        BigDecimal cost,
        String warehouseLocation,
        boolean lowStockAlert,
        Integer reorderPoint,
        Integer reorderQuantity
) implements Serializable {
}
