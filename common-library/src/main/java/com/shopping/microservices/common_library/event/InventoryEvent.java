package com.shopping.microservices.common_library.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopping.microservices.common_library.kafka.BaseEvent;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * Inventory Event for publishing inventory-related domain events.
 * 
 * Used for communication between Inventory Service and other services
 * (Order, Notification).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryEvent extends BaseEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Inventory event types
     */
    public enum InventoryEventType {
        INVENTORY_RESERVED,
        INVENTORY_CONFIRMED,
        INVENTORY_RELEASED,
        INVENTORY_INSUFFICIENT,
        INVENTORY_LOW_STOCK,
        INVENTORY_ADJUSTED
    }

    /**
     * Associated order identifier (nullable for non-order events)
     */
    private Long orderId;

    /**
     * Order number for reference
     */
    private String orderNumber;

    /**
     * List of inventory reservations
     */
    private List<ReservationData> reservations;

    /**
     * Reason for release/adjustment/failure
     */
    private String reason;

    /**
     * Additional metadata for extensibility
     */
    private Map<String, Object> metadata;

    /**
     * Constructor with event type and source
     */
    public InventoryEvent(InventoryEventType eventType, String source) {
        super(eventType.name(), source);
    }

    /**
     * Constructor with event type, source, and correlation ID
     */
    public InventoryEvent(InventoryEventType eventType, String source, String correlationId) {
        super(eventType.name(), source, correlationId);
    }

    /**
     * Nested class representing a single inventory reservation
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ReservationData {

        /**
         * Product identifier
         */
        private Long productId;

        /**
         * Stock Keeping Unit
         */
        private String sku;

        /**
         * Reserved/Released quantity
         */
        private Integer quantity;

        /**
         * Warehouse identifier
         */
        private Long warehouseId;

        /**
         * Warehouse name
         */
        private String warehouseName;

        /**
         * Available quantity (for low stock alerts)
         */
        private Long availableQuantity;

        /**
         * Minimum stock threshold (for low stock alerts)
         */
        private Long minStockThreshold;
    }

    /**
     * Static factory method for creating INVENTORY_RESERVED events
     */
    public static InventoryEvent inventoryReserved(String source, Long orderId, String orderNumber,
                                                   List<ReservationData> reservations) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_RESERVED, source);
        event.setOrderId(orderId);
        event.setOrderNumber(orderNumber);
        event.setReservations(reservations);
        return event;
    }

    /**
     * Static factory method for creating INVENTORY_RELEASED events
     */
    public static InventoryEvent inventoryReleased(String source, Long orderId, String orderNumber,
                                                   List<ReservationData> reservations, 
                                                   String reason) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_RELEASED, source);
        event.setOrderId(orderId);
        event.setOrderNumber(orderNumber);
        event.setReservations(reservations);
        event.setReason(reason);
        return event;
    }
    /**
     * Static factory method for creating INVENTORY_RELEASED events
     */
    public static InventoryEvent inventoryReleased(String source, Long orderId,
                                                   List<ReservationData> reservations,
                                                   String reason) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_RELEASED, source);
        event.setOrderId(orderId);
        event.setReservations(reservations);
        event.setReason(reason);
        return event;
    }

    /**
     * Static factory method for creating INVENTORY_CONFIRMED events
     */
    public static InventoryEvent inventoryConfirmed(String source, Long orderId, String orderNumber,
                                                   List<ReservationData> reservations) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_CONFIRMED, source);
        event.setOrderId(orderId);
        event.setOrderNumber(orderNumber);
        event.setReservations(reservations);
        return event;
    }

    /**
     * Static factory method for creating INVENTORY_INSUFFICIENT events
     */
    public static InventoryEvent inventoryInsufficient(String source, Long orderId, String orderNumber,
                                                       List<ReservationData> reservations) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_INSUFFICIENT, source);
        event.setOrderId(orderId);
        event.setOrderId(orderId);
        event.setReservations(reservations);
        return event;
    }

    /**
     * Static factory method for creating INVENTORY_LOW_STOCK events
     */
    public static InventoryEvent inventoryLowStock(String source, List<ReservationData> lowStockItems) {
        InventoryEvent event = new InventoryEvent(InventoryEventType.INVENTORY_LOW_STOCK, source);
        event.setReservations(lowStockItems);
        return event;
    }
}
