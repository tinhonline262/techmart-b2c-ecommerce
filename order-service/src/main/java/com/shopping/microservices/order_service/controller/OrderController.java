package com.shopping.microservices.order_service.controller;

import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/api/v1/public/orders")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {}", request.customerId());
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Order created successfully", response));
    }

    @PutMapping("/api/v1/public/orders/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderPaymentStatus(
            @Valid @RequestBody UpdateOrderPaymentStatusRequest request) {
        log.info("Updating payment status for order: {}", request.orderId());
        OrderResponse response = orderService.updateOrderPaymentStatus(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Payment status updated", response));
    }

    @GetMapping("/api/v1/public/orders/completed")
    public ResponseEntity<ApiResponse<Boolean>> checkOrderExistsByProductIdAndUserIdWithStatus(
            @RequestParam("productId") Long productId,
            @RequestParam("userId") String userId,
            @RequestParam(value = "status", defaultValue = "COMPLETED") OrderStatus status) {
        boolean exists = orderService.checkOrderExistsByProductIdAndUserIdWithStatus(productId, userId, status);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Check completed", exists));
    }

    @GetMapping("/api/v1/public/orders/my-orders")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getMyOrders(
            @RequestParam("customerId") String customerId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("Getting orders for customer: {}", customerId);
        Page<OrderSummaryResponse> orders = orderService.getMyOrders(customerId, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders retrieved", orders));
    }

    @GetMapping("/api/v1/public/orders/checkout/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByCheckoutId(@PathVariable("id") String id) {
        log.info("Getting order by checkoutId: {}", id);
        OrderResponse response = orderService.getOrderByCheckoutId(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order retrieved", response));
    }

    @GetMapping("/api/v1/orders/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderWithItemsById(@PathVariable("id") Long id) {
        log.info("Getting order with items by ID: {}", id);
        OrderResponse response = orderService.getOrderWithItemsById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order retrieved", response));
    }

    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<Page<OrderSummaryResponse>>> getOrders(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "orderStatus", required = false) OrderStatus orderStatus,
            @RequestParam(value = "paymentStatus", required = false) String paymentStatus,
            @RequestParam(value = "shipmentStatus", required = false) String shipmentStatus,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OrderSummaryResponse> orders = orderService.getOrders(startDate, endDate, productName,
                orderStatus, paymentStatus, shipmentStatus, pageable);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Orders retrieved", orders));
    }

    @GetMapping("/api/v1/orders/latest/{count}")
    public ResponseEntity<ApiResponse<List<OrderSummaryResponse>>> getLatestOrders(@PathVariable("count") int count) {
        List<OrderSummaryResponse> orders = orderService.getLatestOrders(count);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Latest orders retrieved", orders));
    }

    @PutMapping("/api/v1/orders/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable("id") Long id,
            @RequestParam(value = "reason", required = false, defaultValue = "Cancelled by user") String reason) {
        log.info("Cancelling order: {}, reason: {}", id, reason);
        orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order cancelled", null));
    }

    @GetMapping("/api/v1/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderStatus(@PathVariable("id") Long id) {
        log.info("Getting order status for ID: {}", id);
        OrderResponse response = orderService.getOrderWithItemsById(id);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "Order status retrieved", response));
    }

    @PostMapping("/api/v1/orders/csv")
    public ResponseEntity<byte[]> exportCsv(@Valid @RequestBody ExportOrderCsvRequest request) {
        byte[] csvContent = orderService.exportOrdersToCsv(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders_export.csv");
        return ResponseEntity.ok().headers(headers).body(csvContent);
    }
}
