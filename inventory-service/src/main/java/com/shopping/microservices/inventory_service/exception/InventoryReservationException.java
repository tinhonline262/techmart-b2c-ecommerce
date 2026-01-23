package com.shopping.microservices.inventory_service.exception;

public class InventoryReservationException extends RuntimeException{
    public InventoryReservationException(String message, Exception e) {
        super(message, e);
    }
}
