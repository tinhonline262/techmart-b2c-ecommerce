package com.shopping.microservices.order_service.controller;

import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.OrderCreationDTO;
import com.shopping.microservices.order_service.dto.OrderDTO;
import com.shopping.microservices.order_service.servive.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    ApiResponse<OrderDTO> createOrder(@Valid @RequestBody OrderCreationDTO orderCreationDTO) {
        return ApiResponse.success(HttpStatus.CREATED.value(), "Order created successfully", orderService.createOrder(orderCreationDTO));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<List<OrderDTO>> getOrders() {
        return ApiResponse.success(HttpStatus.OK.value(), "Orders retrieved successfully", orderService.getAllOrders());
    }
}
