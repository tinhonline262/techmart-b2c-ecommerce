package com.shopping.microservices.inventory_service.service;

import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;

public interface ReservationService {

    void reserveInventory(OrderEvent orderEvent);

    void confirmReservation(PaymentEvent paymentEvent);

    void releaseReservations(Long orderId);

    void processExpiredReservations();
}
