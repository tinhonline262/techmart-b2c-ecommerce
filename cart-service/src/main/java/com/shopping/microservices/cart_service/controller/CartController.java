package com.shopping.microservices.cart_service.controller;

import com.shopping.microservices.cart_service.dto.ApiResponse;
import com.shopping.microservices.cart_service.dto.CartItemRequestDto;
import com.shopping.microservices.cart_service.dto.CartItemResponseDto;
import com.shopping.microservices.cart_service.repository.CartItemRepository;
import com.shopping.microservices.cart_service.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/v1/public/cart")
@RequiredArgsConstructor
public class CartController {

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> addItemToCart(
            @Valid @RequestBody CartItemRequestDto request,
            HttpServletRequest httpRequest) {
        
        // Get authenticated customer ID from security context
        String customerId = SecurityUtils.getCurrentCustomerId();
        
        // TODO: Implement business logic to add item to cart
        // Placeholder response
        CartItemResponseDto response = CartItemResponseDto.builder()
                .customerId(customerId)
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Item added to cart successfully", response)
        );
    }


    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> updateCartItemQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody CartItemRequestDto request,
            HttpServletRequest httpRequest) {
        
        // Get authenticated customer ID from security context
        String customerId = SecurityUtils.getCurrentCustomerId();
        
        // TODO: Implement business logic to update item quantity
        // Placeholder response
        CartItemResponseDto response = CartItemResponseDto.builder()
                .customerId(customerId)
                .productId(productId)
                .quantity(request.getQuantity())
                .build();
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Cart item quantity updated successfully", response)
        );
    }


    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<CartItemResponseDto>>> getCartItems(
            HttpServletRequest httpRequest) {
        
        // Get authenticated customer ID from security context
        String customerId = SecurityUtils.getCurrentCustomerId();
        
        // TODO: Implement business logic to retrieve cart items
        // Placeholder response
        List<CartItemResponseDto> items = Collections.emptyList();
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Cart items retrieved successfully", items)
        );
    }


    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(
            @PathVariable Long productId,
            HttpServletRequest httpRequest) {
        
        // Get authenticated customer ID from security context
        String customerId = SecurityUtils.getCurrentCustomerId();
        
        // TODO: Implement business logic to remove item from cart
        // Placeholder response
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Item removed from cart successfully", null)
        );
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<Void>> removeMultipleCartItems(
            @RequestParam List<Long> productIds,
            HttpServletRequest httpRequest) {
        
        // Get authenticated customer ID from security context
        String customerId = SecurityUtils.getCurrentCustomerId();
        
        // TODO: Implement business logic to remove multiple items from cart
        // Placeholder response
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Items removed from cart successfully", null)
        );
    }
}
