package com.shopping.microservices.order_service.servive;

import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    public OrderDTO createOrder(OrderCreationDTO orderCreationDTO);
    public List<OrderDTO> getAllOrders();
}
