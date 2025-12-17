package com.shopping.microservices.order_service.servive.impl;

import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;
import com.shopping.microservices.order_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.order_service.dto.product.ReduceStockProductDTO;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.exception.OutOfStockException;
import com.shopping.microservices.order_service.mapper.OrderMapper;
import com.shopping.microservices.order_service.repository.OrderItemRepository;
import com.shopping.microservices.order_service.repository.OrderRepository;
import com.shopping.microservices.order_service.servive.OrderService;
import com.shopping.microservices.order_service.servive.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final KafkaTemplate<String, OrderSendNotificationEvent> kafkaTemplate;

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
                    log.info("Fetched product details for SKU: {}", product);
                    // Check stock availability
                    if (product.quantityInStock() < itemDTO.quantity()) {
                        throw new OutOfStockException("Out of stock for product SKU: " + itemDTO.sku());
                    }
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
        // subtract stock
        orderCreationDTO.items().forEach(itemDTO -> {
            productService.reverseProductStockBySku(ReduceStockProductDTO.builder()
                    .sku(itemDTO.sku())
                    .quantity(itemDTO.quantity())
                    .build());
            log.info("Reduced stock for product SKU: {} by quantity: {}", itemDTO.sku(), itemDTO.quantity());
        });

        log.info("Order created with ID: {}", newOrder.getId());
        kafkaTemplate.send("order-created", mapToOrderNotificationDTO(newOrder));
        log.info("Sent order creation notification for order ID: {} to kafka", newOrder.getId());
        return orderMapper.mapToDTO(newOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        log.info("Retrieving all orders from repository");
        var orders = orderRepository.findAll();
        log.info("Fetched {} orders from repository", orders.size());
        List<OrderDTO> orderDTOs = orders.stream().map(orderMapper::mapToDTO).collect(Collectors.toList());
        log.info("Mapped orders to OrderDTOs");
        return orderDTOs;
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
