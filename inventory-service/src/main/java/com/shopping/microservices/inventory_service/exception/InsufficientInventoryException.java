package com.shopping.microservices.inventory_service.exception;

import lombok.Getter;

public class InsufficientInventoryException extends RuntimeException {
  @Getter
  private final String sku;
  @Getter private final Long productId;
  @Getter private final int requested;
  @Getter private final long available;

  public InsufficientInventoryException(String sku, Long productId, int requested, long available) {
    super(String.format("Insufficient inventory for SKU: %s. Available: %d, Requested: %d",
            sku, available, requested));
    this.sku = sku;
    this.productId = productId;
    this.requested = requested;
    this.available = available;
  }

  public InsufficientInventoryException(String message) {
    super(message);
    this.sku = null;
    this.productId = null;
    this.requested = 0;
    this.available = 0;
  }
}