package com.shopping.microservices.order_service.controller;

import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for order operations.
 * Public APIs: /api/v1/public/orders
 * Admin APIs: /api/v1/orders
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ==================== Public API Endpoints ====================

    /**
     * Create a new order from checkout.
     * POST /api/v1/public/orders
     *
     * @param request the order creation request
     * @return created order response
     */
    @PostMapping("/api/v1/public/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Order created successfully",
                        null // placeholder: orderService.createOrder(request)
                ));
    }

    /**
     * Update order payment status.
     * PUT /api/v1/public/orders/status
     *
     * @param request the payment status update request
     * @return updated order response
     */
    @PutMapping("/api/v1/public/orders/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderPaymentStatus(
            @Valid @RequestBody UpdateOrderPaymentStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Order payment status updated successfully",
                null // placeholder: orderService.updateOrderPaymentStatus(request)
        ));
    }

    /**
     * Check if an order exists for a specific product and user with completed status.
     * GET /api/v1/public/orders/completed
     *
     * @param productId the product ID
     * @param userId    the user/customer ID
     * @return boolean indicating if such order exists
     */
    @GetMapping("/api/v1/public/orders/completed")
    public ResponseEntity<ApiResponse<Boolean>> checkOrderExistsByProductIdAndUserIdWithStatus(
            @RequestParam("productId") Long productId,
            @RequestParam("userId") String userId,
            @RequestParam(value = "status", defaultValue = "COMPLETED") OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Order existence check completed",
                false // placeholder: orderService.checkOrderExistsByProductIdAndUserIdWithStatus(productId, userId, status)
        ));
    }

    /**
     * Get orders for the current authenticated user.
     * GET /api/v1/public/orders/my-orders
     *
     * @param customerId the customer ID (could be extracted from JWT in real implementation)
     * @param pageable   pagination parameters
     * @return page of order summaries
     */
    @GetMapping("/api/v1/public/orders/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getMyOrders(
            @RequestParam("customerId") String customerId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Customer orders retrieved successfully",
                null // placeholder: orderService.getMyOrders(customerId, pageable)
        ));
    }

    /**
     * Get order by checkout ID.
     * GET /api/v1/public/orders/checkout/{id}
     *
     * @param id the checkout ID
     * @return order response
     */
    @GetMapping("/api/v1/public/orders/checkout/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCheckoutId(
            @PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Order retrieved successfully",
                null // placeholder: orderService.getOrderByCheckoutId(id)
        ));
    }

    // ==================== Admin API Endpoints ====================

    /**
     * Get order with items by ID (Admin).
     * GET /api/v1/orders/{id}
     *
     * @param id the order ID
     * @return full order response with items
     */
    @GetMapping("/api/v1/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderWithItemsById(
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Order retrieved successfully",
                null // placeholder: orderService.getOrderWithItemsById(id)
        ));
    }

    /**
     * Get orders with filtering and pagination (Admin).
     * GET /api/v1/orders
     *
     * @param startDate      optional start date filter
     * @param endDate        optional end date filter
     * @param productName    optional product name filter
     * @param orderStatus    optional order status filter
     * @param paymentStatus  optional payment status filter
     * @param shipmentStatus optional shipment status filter
     * @param pageable       pagination parameters
     * @return page of order summaries
     */
    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getOrders(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
            @RequestParam(value = "paymentStatus", required = false) String paymentStatus,
            @RequestParam(value = "shipmentStatus", required = false) String shipmentStatus,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Orders retrieved successfully",
                null // placeholder: orderService.getOrders(startDate, endDate, productName, orderStatus, paymentStatus, shipmentStatus, pageable)
        ));
    }

    /**
     * Get latest orders (Admin).
     * GET /api/v1/orders/latest/{count}
     *
     * @param count number of latest orders to retrieve
     * @return list of latest order summaries
     */
    @GetMapping("/api/v1/orders/latest/{count}")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getLatestOrders(
            @PathVariable("count") int count) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Latest orders retrieved successfully",
                null // placeholder: orderService.getLatestOrders(count)
        ));
    }

    /**
     * Export orders to CSV (Admin).
     * POST /api/v1/orders/csv
     *
     * @param request the export request with filters
     * @return CSV file as byte array
     */
    @PostMapping("/api/v1/orders/csv")
    public ResponseEntity<byte[]> exportCsv(
            @Valid @RequestBody ExportOrderCsvRequest request) {
        byte[] csvContent = new byte[0]; // placeholder: orderService.exportOrdersToCsv(request)

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders_export.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent);
    }
}
