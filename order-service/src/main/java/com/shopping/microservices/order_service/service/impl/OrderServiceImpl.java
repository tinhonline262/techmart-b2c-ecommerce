package com.shopping.microservices.order_service.service.impl;

import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.exception.ResourceNotFoundException;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderAddress;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.enumeration.OrderProgress;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.enumeration.PaymentStatus;
import com.shopping.microservices.order_service.enumeration.ShipmentStatus;
import com.shopping.microservices.order_service.mapper.OrderMapper;
import com.shopping.microservices.order_service.repository.OrderRepository;
import com.shopping.microservices.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final EventPublisher eventPublisher;

    private static final String SERVICE_NAME = "order-service";

    @Override
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order for customer: {}, checkoutId: {}", request.customerId(), request.checkoutId());

        Order order = orderMapper.toEntity(request);
        order.setCheckoutId(request.checkoutId());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShipmentStatus(ShipmentStatus.PENDING);
        order.setProgress(OrderProgress.CREATED);

        if (request.shippingAddress() != null) {
            OrderAddress shippingAddress = orderMapper.toEntity(request.shippingAddress());
            order.setShippingAddress(shippingAddress);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (request.items() != null) {
            for (OrderItemRequest itemRequest : request.items()) {
                OrderItem item = orderMapper.toEntity(itemRequest);
                item.setSku("SKU-" + itemRequest.productId());
                order.addItem(item);
                
                BigDecimal itemTotal = itemRequest.price().multiply(BigDecimal.valueOf(itemRequest.quantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        order.setTotalAmount(totalAmount);
        order.setNumberItem(order.getItems().size());

        order = orderRepository.save(order);
        log.info("Order created with ID: {}", order.getId());

        publishOrderCreatedEvent(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public void updateOrderProgress(Long orderId, OrderProgress progress, String error) {
        log.info("Updating order {} progress to: {}", orderId, progress);

        Order order = findOrderById(orderId);
        order.setProgress(progress);

        if (error != null) {
            Map<String, Object> lastError = new HashMap<>();
            lastError.put("message", error);
            lastError.put("timestamp", System.currentTimeMillis());
            order.setLastError(lastError);
        }

        orderRepository.save(order);
        log.info("Updated order {} progress to: {}", orderId, progress);
    }

    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        log.info("Confirming order: {}", orderId);

        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setProgress(OrderProgress.PAYMENT_COMPLETED);

        orderRepository.save(order);
        log.info("Order confirmed: {}", orderId);

        publishOrderConfirmedEvent(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId, String message) {
        log.info("Completing order: {}, message: {}", orderId, message);

        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.COMPLETED);
        order.setProgress(OrderProgress.COMPLETED);

        orderRepository.save(order);
        log.info("Order completed: {}", orderId);

        publishOrderConfirmedEvent(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {}, reason: {}", orderId, reason);

        Order order = findOrderById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        order.setRejectReason(reason);

        orderRepository.save(order);
        log.info("Order cancelled: {}", orderId);

        publishOrderCancelledEvent(order, reason);
    }

    @Override
    public OrderResponse findOrderByCheckoutId(String checkoutId) {
        log.info("Finding order by checkoutId: {}", checkoutId);
        Order order = orderRepository.findByCheckoutId(checkoutId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "checkoutId", checkoutId));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderPaymentStatus(UpdateOrderPaymentStatusRequest request) {
        log.info("Updating payment status for order: {}", request.orderId());

        Order order = findOrderById(request.orderId());
        orderMapper.updatePaymentStatus(request, order);
        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkOrderExistsByProductIdAndUserIdWithStatus(Long productId, String userId, OrderStatus status) {
        return orderRepository.existsByProductIdAndCustomerIdAndStatus(productId, userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getMyOrders(String customerId, Pageable pageable) {
        log.info("Getting orders for customer: {}", customerId);
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(orderMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByCheckoutId(String checkoutId) {
        log.info("Getting order by checkoutId: {}", checkoutId);
        return orderRepository.findByCheckoutId(checkoutId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "checkoutId", checkoutId));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderWithItemsById(Long orderId) {
        log.info("Getting order with items by ID: {}", orderId);
        Order order = orderRepository.findOrderWithItems(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));

        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());

        OrderAddressResponse addressResponse = order.getShippingAddress() != null
                ? orderMapper.toResponse(order.getShippingAddress())
                : null;

        return orderMapper.toResponseWithDetails(order, itemResponses, addressResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrders(LocalDate startDate, LocalDate endDate, String productName,
                                                 OrderStatus orderStatus, String paymentStatus,
                                                 String shipmentStatus, Pageable pageable) {
        var startInstant = startDate != null ? startDate.atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        var endInstant = endDate != null ? endDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant() : null;
        var paymentStatusEnum = paymentStatus != null ? PaymentStatus.valueOf(paymentStatus) : null;

        return orderRepository.findOrdersWithFilters(startInstant, endInstant, orderStatus, paymentStatusEnum, pageable)
                .map(orderMapper::toSummaryResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> getLatestOrders(int count) {
        log.info("Getting latest {} orders", count);
        return orderRepository.findAll(PageRequest.of(0, count, org.springframework.data.domain.Sort.by("createdAt").descending()))
                .stream()
                .map(orderMapper::toSummaryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportOrdersToCsv(ExportOrderCsvRequest request) {
        log.info("Exporting orders to CSV");
        // TODO: Implement CSV export
        return new byte[0];
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long orderId) {
        return orderRepository.existsById(orderId);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order {} status to: {}", orderId, status);
        Order order = findOrderById(orderId);
        order.setStatus(status);
        order = orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.order(orderId));
    }

    private void publishOrderCreatedEvent(Order order) {
        List<OrderEvent.OrderItemData> items = order.getItems().stream()
                .map(item -> OrderEvent.OrderItemData.builder()
                        .productId(item.getProductId())
                        .sku(item.getSku())
                        .name(item.getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());

        Map<String, Object> processingState = new HashMap<>();
        processingState.put("progress", order.getProgress().name());
        processingState.put("paymentMethod", order.getPaymentMethodId());
        OrderEvent event = OrderEvent.orderCreated(
                SERVICE_NAME,
                order.getId(),
                order.getCustomerId(),
                order.getEmail(),
                order.getTotalAmount(),
                items,
                processingState
        );
        event.setOrderNumber("ORD-" + order.getId());

        eventPublisher.publish(KafkaTopics.ORDER_EVENTS, event);
        log.info("Published ORDER_CREATED event for order: {}", order.getId());
    }

    private void publishOrderConfirmedEvent(Order order) {
        OrderEvent event = OrderEvent.orderConfirmed(SERVICE_NAME, order.getId(), order.getCustomerId(), order.getEmail());
        event.setOrderNumber("ORD-" + order.getId());
        event.setTotalAmount(order.getTotalAmount());

        eventPublisher.publish(KafkaTopics.ORDER_EVENTS, event);
        log.info("Published ORDER_CONFIRMED event for order: {}", order.getId());
    }

    private void publishOrderCancelledEvent(Order order, String reason) {
        OrderEvent event = OrderEvent.orderCancelled(SERVICE_NAME, order.getId(), reason);
        event.setOrderNumber("ORD-" + order.getId());
        event.setCustomerId(order.getCustomerId());
        event.setEmail(order.getEmail());

        eventPublisher.publish(KafkaTopics.ORDER_EVENTS, event);
        log.info("Published ORDER_CANCELLED event for order: {}", order.getId());
    }
}
