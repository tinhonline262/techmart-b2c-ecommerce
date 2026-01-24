package com.shopping.microservices.order_service.service;

import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.enumeration.OrderProgress;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse updateOrderPaymentStatus(UpdateOrderPaymentStatusRequest request);

    boolean checkOrderExistsByProductIdAndUserIdWithStatus(Long productId, String userId, OrderStatus status);

    Page<OrderSummaryResponse> getMyOrders(String customerId, Pageable pageable);

    OrderResponse getOrderByCheckoutId(String checkoutId);

    OrderResponse getOrderWithItemsById(Long orderId);

    Page<OrderSummaryResponse> getOrders(LocalDate startDate, LocalDate endDate, String productName,
                                         OrderStatus orderStatus, String paymentStatus,
                                         String shipmentStatus, Pageable pageable);

    List<OrderSummaryResponse> getLatestOrders(int count);

    byte[] exportOrdersToCsv(ExportOrderCsvRequest request);

    List<OrderResponse> getAllOrders();

    boolean existsById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    // ==================== SAGA Orchestration Methods ====================

    void updateOrderProgress(Long orderId, OrderProgress progress, String error);

    void confirmOrder(Long orderId);

    void completeOrder(Long orderId, String message);

    void cancelOrder(Long orderId, String reason);

    OrderResponse findOrderByCheckoutId(String checkoutId);
}
