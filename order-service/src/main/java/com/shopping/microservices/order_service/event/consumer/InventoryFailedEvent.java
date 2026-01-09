package com.shopping.microservices.order_service.event.consumer;

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

