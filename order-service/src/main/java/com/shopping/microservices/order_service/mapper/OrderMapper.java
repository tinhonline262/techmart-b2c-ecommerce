package com.shopping.microservices.order_service.mapper;

import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;
import com.shopping.microservices.order_service.dto.OrderItemDTO;
import com.shopping.microservices.order_service.dto.OrderStatusHistoryDTO;
import com.shopping.microservices.order_service.dto.product.ProductDTO;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.entity.OrderStatusHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    public Order mapToEntity(OrderCreationDTO orderCreationDTO, List<OrderItem> orderItems) {
        return Order.builder()
                .orderNumber(orderCreationDTO.customerId() + "-" + System.currentTimeMillis())
                .customerId(orderCreationDTO.customerId())
                .customerName(orderCreationDTO.customerName())
                .customerEmail(orderCreationDTO.customerEmail())
                .orderItems(new HashSet<>(orderItems))
                .totalAmount(orderItems.stream()
                        .map(OrderItem::getTotalPrice)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add) )
                .build();
    }
    public OrderDTO mapToDTO(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getOrderDate(),
                order.getStatus(),
                order.getTotalAmount()
        );
    }

    private OrderStatusHistoryDTO mapOrderStatusHistoryToDTO(OrderStatusHistory orderStatusHistory) {
        return new OrderStatusHistoryDTO(
                orderStatusHistory.getId(),
                orderStatusHistory.getStatus(),
                orderStatusHistory.getComments()
        );
    }

    private OrderItemDTO mapOrderItemToDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProductSku(),
                orderItem.getQuantity()
        );
    }

    public OrderItem mapToOrderItemEntity(OrderItemDTO orderItemDTO, ProductDTO product) {
        return OrderItem.builder()
                .productId(product.id())
                .productName(product.name())
                .productSku(product.sku())
                .quantity(orderItemDTO.quantity())
                .unitPrice(product.price())
                .totalPrice(product.price().multiply(java.math.BigDecimal.valueOf(orderItemDTO.quantity())))
                .build();
    }
}
