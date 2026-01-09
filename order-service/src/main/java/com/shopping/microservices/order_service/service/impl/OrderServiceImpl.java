package com.shopping.microservices.order_service.service.impl;

import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.event.producer.OrderCancelledEvent;
import com.shopping.microservices.order_service.event.producer.OrderCompletedEvent;
import com.shopping.microservices.order_service.event.producer.OrderCreatedEvent;
import com.shopping.microservices.order_service.event.producer.OrderSendNotificationEvent;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.kafka.producer.NotificationEventProducer;
import com.shopping.microservices.order_service.kafka.producer.OrderEventProducer;
import com.shopping.microservices.order_service.mapper.OrderMapper;
import com.shopping.microservices.order_service.repository.OrderItemRepository;
import com.shopping.microservices.order_service.repository.OrderRepository;
import com.shopping.microservices.order_service.service.OrderService;
import com.shopping.microservices.order_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final NotificationEventProducer notificationEventProducer;


    @Override
    @Transactional
    public OrderDTO createOrder(OrderCreationDTO orderCreationDTO) {
        log.info("Creating order for customer: {}", orderCreationDTO.customerId());
        Order newOrder = orderMapper.mapToEntity(orderCreationDTO, new ArrayList<>());
        orderRepository.save(newOrder);

        Set<OrderItem> orderItems = orderCreationDTO.items().stream()
                .map(itemDTO -> {
                    // Fetch product details
                    var product = productService.getProductBySku(itemDTO.sku()).getData();
                    OrderItem item =  orderMapper.mapToOrderItemEntity(itemDTO, product);
                    item.setOrder(newOrder);
                    return item;
                })
                .collect(Collectors.toSet());
        orderItemRepository.saveAll(orderItems);
        newOrder.setTotalAmount(orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        orderRepository.save(newOrder);

        log.info("Order created with ID: {}", newOrder.getId());
        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .orderId(newOrder.getId())
                .customerId(newOrder.getCustomerId())
                .orderNumber(newOrder.getOrderNumber())
                .orderDate(newOrder.getOrderDate())
                .status(newOrder.getStatus())
                .totalAmount(newOrder.getTotalAmount())
                .orderItems(orderItems.stream()
                        .map(item -> new OrderCreatedEvent.OrderItemData(
                                item.getProductId(),
                                item.getSku(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice(),
                                item.getTotalPrice()
                        ))
                        .collect(Collectors.toList()))
                .paymentMethod(orderCreationDTO.paymentMethod())
                .build();

        //send order created event to kafka
        orderEventProducer.publishOrderCreatedEvent(orderCreatedEvent);
        log.info("Sent order created event for order ID: {} to event producer", newOrder.getId());

        //send notification event to kafka
//        notificationEventProducer.publishOrderCreatedNotificationEvent(mapToOrderNotificationDTO(newOrder));
//        log.info("Sent order creation notification for order ID: {} to kafka", newOrder.getId());
        return orderMapper.mapToDTO(newOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        log.info("Retrieving all orders from repository");
        var orders = orderRepository.findAll();
        List<OrderDTO> orderDTOs = orders.stream().map(orderMapper::mapToDTO).collect(Collectors.toList());
        log.info("Retrieved {} orders", orderDTOs.size());
        return orderDTOs;
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status for order ID: {}", orderId);
        var orderToUpdate = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        orderToUpdate.setStatus(status.name());
        orderRepository.save(orderToUpdate);
        if (status == OrderStatus.COMPLETED) {
            OrderCompletedEvent event = OrderCompletedEvent.builder()
                    .orderId(orderToUpdate.getId())
                    .orderNumber(orderToUpdate.getOrderNumber())
                    .customerId(orderToUpdate.getCustomerId())
                    .customerName(orderToUpdate.getCustomerName())
                    .customerEmail(orderToUpdate.getCustomerEmail())
                    .completedAt(Instant.now())
                    .totalAmount(orderToUpdate.getTotalAmount())
                    .message("Order completed successfully")
                    .build();
            orderEventProducer.publishOrderCompletedEvent(event);
        }
        log.info("Order status updated to {} for order ID: {}", status, orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findByOrderId(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Order findByOrderNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with orderNumber: " + orderNumber));
    }

    @Override
    public void completeOrder(Long orderId, String message) {
        log.info("Completing order with id: {}", orderId);

        Order order = findByOrderId(orderId);

        // Update order status to COMPLETED
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        log.info("Order {} status updated to COMPLETED", order.getOrderNumber());

        // Publish OrderCompletedEvent
        OrderCompletedEvent event = OrderCompletedEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .completedAt(Instant.now())
                .totalAmount(order.getTotalAmount())
                .message(message != null ? message : "Order completed successfully")
                .build();

        orderEventProducer.publishOrderCompletedEvent(event);
        log.info("OrderCompletedEvent published for order: {}", order.getOrderNumber());
    }

    @Override
    public void cancelOrder(Long orderId, String reason) {
        log.warn("Cancelling order with id: {}, reason: {}", orderId, reason);

        Order order = findByOrderId(orderId);

        // Update order status to CANCELLED
        order.setStatus("CANCELLED");
        orderRepository.save(order);

        log.info("Order {} status updated to CANCELLED", order.getOrderNumber());

        // Publish OrderCancelledEvent
        OrderCancelledEvent event = OrderCancelledEvent.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .customerId(order.getCustomerId())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .cancelledAt(Instant.now())
                .totalAmount(order.getTotalAmount())
                .reason(reason != null ? reason : "Order cancelled")
                .build();

        orderEventProducer.publishOrderCancelledEvent(event);
        log.info("OrderCancelledEvent published for order: {}", order.getOrderNumber());
    }

    private OrderSendNotificationEvent mapToOrderNotificationDTO(Order order) {
        return OrderSendNotificationEvent.builder()
                .orderNumber(order.getOrderNumber())
                .customerName(order.getCustomerName())
                .customerEmail(order.getCustomerEmail())
                .orderDate(order.getOrderDate())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .build();
    }
}
