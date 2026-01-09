package com.shopping.microservices.order_service.service;

import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.enumeration.OrderStatus;

import java.util.List;

public interface OrderService {
    public OrderDTO createOrder(OrderCreationDTO orderCreationDTO);
    public List<OrderDTO> getAllOrders();
    public void updateOrderStatus(Long orderId, OrderStatus status);
    Order findByOrderId(Long orderId);

    Order findByOrderNumber(String orderNumber);

    void completeOrder(Long orderId, String message);

    void cancelOrder(Long orderId, String reason);
}
