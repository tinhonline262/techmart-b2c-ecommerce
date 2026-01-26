package com.shopping.microservices.common_library.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when a requested resource is not found.
 * 
 * Default HTTP status: 404 NOT_FOUND
 */
@Getter
public class ResourceNotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    private static final HttpStatus DEFAULT_STATUS = HttpStatus.NOT_FOUND;

    /**
     * Name of the resource type (e.g., "Product", "Order")
     */
    private final String resourceName;

    /**
     * Identifier of the resource (e.g., ID, SKU)
     */
    private final Object resourceId;

    /**
     * Constructor with resource name and ID
     *
     * @param resourceName Type of resource
     * @param resourceId Resource identifier
     */
    public ResourceNotFoundException(String resourceName, Object resourceId) {
        super(
                String.format("%s not found with id: %s", resourceName, resourceId),
                "RESOURCE_NOT_FOUND",
                DEFAULT_STATUS
        );
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    /**
     * Constructor with resource name, field name, and field value
     *
     * @param resourceName Type of resource
     * @param fieldName Name of the field used for lookup
     * @param fieldValue Value of the field
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(
                String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
                "RESOURCE_NOT_FOUND",
                DEFAULT_STATUS
        );
        this.resourceName = resourceName;
        this.resourceId = fieldValue;
    }

    /**
     * Constructor with custom message
     *
     * @param message Custom error message
     */
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND", DEFAULT_STATUS);
        this.resourceName = null;
        this.resourceId = null;
    }

    /**
     * Static factory method for Product not found
     */
    public static ResourceNotFoundException product(Long productId) {
        return new ResourceNotFoundException("Product", productId);
    }

    /**
     * Static factory method for Product not found by SKU
     */
    public static ResourceNotFoundException productBySku(String sku) {
        return new ResourceNotFoundException("Product", "sku", sku);
    }

    /**
     * Static factory method for Order not found
     */
    public static ResourceNotFoundException order(Long orderId) {
        return new ResourceNotFoundException("Order", orderId);
    }

    /**
     * Static factory method for Order not found by order number
     */
    public static ResourceNotFoundException orderByNumber(String orderNumber) {
        return new ResourceNotFoundException("Order", "orderNumber", orderNumber);
    }

    /**
     * Static factory method for Customer not found
     */
    public static ResourceNotFoundException customer(String customerId) {
        return new ResourceNotFoundException("Customer", customerId);
    }

    /**
     * Static factory method for Category not found
     */
    public static ResourceNotFoundException category(Long categoryId) {
        return new ResourceNotFoundException("Category", categoryId);
    }

    /**
     * Static factory method for Payment not found
     */
    public static ResourceNotFoundException payment(Long paymentId) {
        return new ResourceNotFoundException("Payment", paymentId);
    }
}
