package com.shopping.microservices.payment_service.event;

import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Event received when inventory is reserved successfully
 */
@Builder
public record InventoryReservedEvent(
        Long orderId,
        String orderNumber,
        List<ReservedItem> reservedItems,
        String message
) implements Serializable {

    @Builder
    public record ReservedItem(
            Long productId,
            String sku,
            Integer quantity
    ) implements Serializable {
    }
}

