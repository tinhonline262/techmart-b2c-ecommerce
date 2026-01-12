package com.shopping.microservices.order_service.service;

import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface OrderService {

    // ==================== Public API Methods ====================

    /**
     * Create a new order from checkout
     *
     * @param request the order creation request
     * @return the created order response
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Update order payment status
     *
     * @param request the payment status update request
     * @return the updated order response
     */
    OrderResponse updateOrderPaymentStatus(UpdateOrderPaymentStatusRequest request);

    /**
     * Check if an order exists for a specific product and user with given status
     *
     * @param productId the product ID
     * @param userId    the user/customer ID
     * @param status    the order status to check
     * @return true if such order exists
     */
    boolean checkOrderExistsByProductIdAndUserIdWithStatus(Long productId, String userId, OrderStatus status);

    /**
     * Get orders for the current authenticated user
     *
     * @param customerId the customer ID
     * @param pageable   pagination parameters
     * @return page of order summaries
     */
    Page<OrderSummaryResponse> getMyOrders(String customerId, Pageable pageable);

    /**
     * Get order by checkout ID
     *
     * @param checkoutId the checkout ID
     * @return the order response
     */
    OrderResponse getOrderByCheckoutId(String checkoutId);

    // ==================== Admin API Methods ====================

    /**
     * Get order with items by ID (Admin)
     *
     * @param orderId the order ID
     * @return the full order response with items
     */
    OrderResponse getOrderWithItemsById(Long orderId);

    /**
     * Get orders with filtering and pagination (Admin)
     *
     * @param startDate      optional start date filter
     * @param endDate        optional end date filter
     * @param productName    optional product name filter
     * @param orderStatus    optional order status filter
     * @param paymentStatus  optional payment status filter
     * @param shipmentStatus optional shipment status filter
     * @param pageable       pagination parameters
     * @return page of order responses
     */
    Page<OrderSummaryResponse> getOrders(LocalDate startDate, LocalDate endDate, String productName,
                                         OrderStatus orderStatus, String paymentStatus,
                                         String shipmentStatus, Pageable pageable);

    /**
     * Get latest orders (Admin)
     *
     * @param count number of latest orders to retrieve
     * @return list of latest order summaries
     */
    List<OrderSummaryResponse> getLatestOrders(int count);

    /**
     * Export orders to CSV (Admin)
     *
     * @param request the export request with filters
     * @return CSV content as byte array
     */
    byte[] exportOrdersToCsv(ExportOrderCsvRequest request);

    // ==================== Utility Methods ====================

    /**
     * Get all orders (legacy method)
     *
     * @return list of all orders
     */
    List<OrderResponse> getAllOrders();

    /**
     * Check if order exists by ID
     *
     * @param orderId the order ID
     * @return true if order exists
     */
    boolean existsById(Long orderId);

    /**
     * Update order status
     *
     * @param orderId the order ID
     * @param status  the new status
     * @return the updated order response
     */
    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);
}
