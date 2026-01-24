package com.shopping.microservices.common_library.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopping.microservices.common_library.kafka.BaseEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Order Event for publishing order-related domain events.
 * 
 * Used for communication between Order Service and other services
 * (Payment, Inventory, Notification).
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderEvent extends BaseEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Order event types
     */
    public enum OrderEventType {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        ORDER_FAILED,
        ORDER_SHIPPED,
        ORDER_DELIVERED
    }

    /**
     * Unique order identifier
     */
    private Long orderId;

    /**
     * Order number for display purposes
     */
    private String orderNumber;

    /**
     * Customer identifier
     */
    private String customerId;

    /**
     * Customer email for notifications
     */
    private String email;

    /**
     * Total order amount
     */
    private BigDecimal totalAmount;

    /**
     * List of items in the order
     */
    private List<OrderItemData> items;

    /**
     * Reason for cancellation or failure
     */
    private String reason;

    /**
     * Additional metadata for extensibility
     */
    private Map<String, Object> metadata;

    /**
     * Constructor with event type and source
     */
    public OrderEvent(OrderEventType eventType, String source) {
        super(eventType.name(), source);
    }

    /**
     * Constructor with event type, source, and correlation ID
     */
    public OrderEvent(OrderEventType eventType, String source, String correlationId) {
        super(eventType.name(), source, correlationId);
    }

    /**
     * Nested class representing an order item
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class OrderItemData {
        
        /**
         * Product identifier
         */
        private Long productId;

        /**
         * Stock Keeping Unit
         */
        private String sku;

        /**
         * Product name
         */
        private String name;

        /**
         * Quantity ordered
         */
        private Integer quantity;

        /**
         * Unit price
         */
        private BigDecimal price;
    }

    /**
     * Static factory method for creating ORDER_CREATED events
     */
    public static OrderEvent orderCreated(String source, Long orderId, String customerId, 
                                          String email, BigDecimal totalAmount, 
                                          List<OrderItemData> items, Map<String, Object> metadata) {
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CREATED, source);
        event.setOrderId(orderId);
        event.setCustomerId(customerId);
        event.setEmail(email);
        event.setTotalAmount(totalAmount);
        event.setItems(items);
        event.setMetadata(metadata);
        return event;
    }

    /**
     * Static factory method for creating ORDER_CONFIRMED events
     */
    public static OrderEvent orderConfirmed(String source, Long orderId, String customerId, String email) {
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CONFIRMED, source);
        event.setOrderId(orderId);
        event.setCustomerId(customerId);
        event.setEmail(email);
        return event;
    }

    /**
     * Static factory method for creating ORDER_CANCELLED events
     */
    public static OrderEvent orderCancelled(String source, Long orderId, String reason) {
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_CANCELLED, source);
        event.setOrderId(orderId);
        event.setReason(reason);
        return event;
    }

    /**
     * Static factory method for creating ORDER_FAILED events
     */
    public static OrderEvent orderFailed(String source, Long orderId, String reason) {
        OrderEvent event = new OrderEvent(OrderEventType.ORDER_FAILED, source);
        event.setOrderId(orderId);
        event.setReason(reason);
        return event;
    }
}
