package com.shopping.microservices.order_service.enumeration;

public enum OrderProgress {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    READY_TO_SHIP,
    COMPLETED
}
