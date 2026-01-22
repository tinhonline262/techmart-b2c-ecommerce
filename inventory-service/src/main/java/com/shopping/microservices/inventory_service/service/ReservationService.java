package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.common_library.event.OrderEvent;

public interface ReservationService {

    void reserveInventory(OrderEvent orderEvent);

    void confirmReservation(String orderId);

    void releaseReservations(String orderId);

    void processExpiredReservations();
}
