package com.shopping.microservices.common_library.exception;

import lombok.Getter;

/**
 * Exception thrown when inventory is insufficient to fulfill an order.
 * 
 * Extends BusinessException with additional inventory-specific details.
 */
@Getter
public class InsufficientInventoryException extends BusinessException {

    private static final long serialVersionUID = 1L;

    /**
     * Product identifier
     */
    private final Long productId;

    /**
     * Product SKU
     */
    private final String sku;

    /**
     * Quantity requested
     */
    private final Integer requestedQuantity;

    /**
     * Quantity available
     */
    private final Integer availableQuantity;

    /**
     * Constructor with product ID and quantities
     *
     * @param productId Product identifier
     * @param requestedQuantity Requested quantity
     * @param availableQuantity Available quantity
     */
    public InsufficientInventoryException(Long productId, Integer requestedQuantity, 
                                          Integer availableQuantity) {
        super(
                String.format("Insufficient inventory for product %d: requested %d, available %d",
                        productId, requestedQuantity, availableQuantity),
                "INSUFFICIENT_INVENTORY"
        );
        this.productId = productId;
        this.sku = null;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Constructor with SKU and quantities
     *
     * @param sku Product SKU
     * @param requestedQuantity Requested quantity
     * @param availableQuantity Available quantity
     */
    public InsufficientInventoryException(String sku, Integer requestedQuantity, 
                                          Integer availableQuantity) {
        super(
                String.format("Insufficient inventory for SKU %s: requested %d, available %d",
                        sku, requestedQuantity, availableQuantity),
                "INSUFFICIENT_INVENTORY"
        );
        this.productId = null;
        this.sku = sku;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Constructor with all details
     *
     * @param productId Product identifier
     * @param sku Product SKU
     * @param requestedQuantity Requested quantity
     * @param availableQuantity Available quantity
     */
    public InsufficientInventoryException(Long productId, String sku, 
                                          Integer requestedQuantity, Integer availableQuantity) {
        super(
                String.format("Insufficient inventory for product %d (SKU: %s): requested %d, available %d",
                        productId, sku, requestedQuantity, availableQuantity),
                "INSUFFICIENT_INVENTORY"
        );
        this.productId = productId;
        this.sku = sku;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }

    /**
     * Get the shortage (how many items are missing)
     */
    public int getShortage() {
        return requestedQuantity - availableQuantity;
    }
}
