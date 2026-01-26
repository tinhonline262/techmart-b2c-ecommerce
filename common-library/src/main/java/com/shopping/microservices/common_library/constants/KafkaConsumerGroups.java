package com.shopping.microservices.common_library.constants;

/**
 * Kafka consumer group IDs used across microservices.
 * 
 * Each service should have a unique consumer group to ensure
 * proper message distribution and parallel processing.
 */
public final class KafkaConsumerGroups {

    // ===========================================
    // Service Consumer Groups
    // ===========================================
    
    /**
     * Consumer group for Order Service
     */
    public static final String ORDER_SERVICE = "order-service-group";
    
    /**
     * Consumer group for Payment Service
     */
    public static final String PAYMENT_SERVICE = "payment-service-group";
    
    /**
     * Consumer group for Inventory Service
     */
    public static final String INVENTORY_SERVICE = "inventory-service-group";
    
    /**
     * Consumer group for Notification Service
     */
    public static final String NOTIFICATION_SERVICE = "notification-service-group";
    
    /**
     * Consumer group for Product Service
     */
    public static final String PRODUCT_SERVICE = "product-service-group";
    
    /**
     * Consumer group for Shipping Service
     */
    public static final String SHIPPING_SERVICE = "shipping-service-group";
    
    /**
     * Consumer group for Analytics Service
     */
    public static final String ANALYTICS_SERVICE = "analytics-service-group";

    // ===========================================
    // Specialized Consumer Groups
    // ===========================================
    
    /**
     * Consumer group for email notifications
     */
    public static final String EMAIL_NOTIFICATION_GROUP = "email-notification-group";
    
    /**
     * Consumer group for SMS notifications
     */
    public static final String SMS_NOTIFICATION_GROUP = "sms-notification-group";
    
    /**
     * Consumer group for push notifications
     */
    public static final String PUSH_NOTIFICATION_GROUP = "push-notification-group";
    
    /**
     * Consumer group for dead letter queue processing
     */
    public static final String DLQ_PROCESSOR_GROUP = "dlq-processor-group";

    // Private constructor to prevent instantiation
    private KafkaConsumerGroups() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
