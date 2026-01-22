package com.shopping.microservices.common_library.constants;

/**
 * Kafka topic names used across microservices.
 * 
 * All services should use these constants to ensure
 * consistent topic naming and easy refactoring.
 */
public final class KafkaTopics {

    // ===========================================
    // Order Service Topics
    // ===========================================
    
    /**
     * Topic for order-related events (created, confirmed, cancelled, etc.)
     */
    public static final String ORDER_EVENTS = "order-events";
    
    /**
     * Topic for order status updates
     */
    public static final String ORDER_STATUS_UPDATES = "order-status-updates";

    // ===========================================
    // Payment Service Topics
    // ===========================================
    
    /**
     * Topic for payment-related events (initiated, success, failed, etc.)
     */
    public static final String PAYMENT_EVENTS = "payment-events";
    
    /**
     * Topic for payment status updates
     */
    public static final String PAYMENT_STATUS_UPDATES = "payment-status-updates";

    // ===========================================
    // Inventory Service Topics
    // ===========================================
    
    /**
     * Topic for inventory-related events (reserved, released, low stock, etc.)
     */
    public static final String INVENTORY_EVENTS = "inventory-events";
    
    /**
     * Topic for inventory adjustment notifications
     */
    public static final String INVENTORY_ADJUSTMENTS = "inventory-adjustments";

    // ===========================================
    // Notification Service Topics
    // ===========================================
    
    /**
     * Topic for notification requests (email, SMS, push)
     */
    public static final String NOTIFICATION_EVENTS = "notification-events";
    
    /**
     * Topic for email notifications
     */
    public static final String EMAIL_NOTIFICATIONS = "email-notifications";
    
    /**
     * Topic for SMS notifications
     */
    public static final String SMS_NOTIFICATIONS = "sms-notifications";

    // ===========================================
    // Product Service Topics
    // ===========================================
    
    /**
     * Topic for product-related events (created, updated, deleted)
     */
    public static final String PRODUCT_EVENTS = "product-events";
    
    /**
     * Topic for product price changes
     */
    public static final String PRICE_CHANGE_EVENTS = "price-change-events";

    // ===========================================
    // Dead Letter Topics
    // ===========================================
    
    /**
     * Dead letter topic for failed order events
     */
    public static final String ORDER_EVENTS_DLT = "order-events.DLT";
    
    /**
     * Dead letter topic for failed payment events
     */
    public static final String PAYMENT_EVENTS_DLT = "payment-events.DLT";
    
    /**
     * Dead letter topic for failed inventory events
     */
    public static final String INVENTORY_EVENTS_DLT = "inventory-events.DLT";
    
    /**
     * Dead letter topic for failed notification events
     */
    public static final String NOTIFICATION_EVENTS_DLT = "notification-events.DLT";

    // Private constructor to prevent instantiation
    private KafkaTopics() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
