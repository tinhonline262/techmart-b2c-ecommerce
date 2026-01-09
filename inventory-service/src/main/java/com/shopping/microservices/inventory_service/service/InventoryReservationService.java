package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.inventory_service.event.InventoryReservedEvent;
import com.shopping.microservices.inventory_service.event.OrderCreatedEvent;

/**
 * Service for managing inventory reservations via Kafka events
 */

public interface InventoryReservationService {


    public boolean processOrderReservation(OrderCreatedEvent event);

    public InventoryReservedEvent buildReservedEvent(OrderCreatedEvent orderEvent);

    public void confirmReservation(String orderId);

    public void cancelReservation(String orderId);

    public void processExpiredReservations();

    public int cleanupOldReservations(int daysOld);
}

