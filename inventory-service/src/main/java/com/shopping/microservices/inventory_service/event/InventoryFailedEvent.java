package com.shopping.microservices.inventory_service.event;

import lombok.Builder;

import java.io.Serializable;

/**
 * Event published when inventory reservation fails
 */
@Builder
public record InventoryFailedEvent(
        Long orderId,
        String orderNumber,
        String reason,
        String failedSku
) implements Serializable {
}

