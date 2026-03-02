package com.shopping.microservices.cart_service.controller;

import com.shopping.microservices.cart_service.dto.ApiResponse;
import com.shopping.microservices.cart_service.dto.CartItemRequestDto;
import com.shopping.microservices.cart_service.dto.CartItemResponseDto;
import com.shopping.microservices.cart_service.dto.EnrichedCartItemResponseDto;
import com.shopping.microservices.cart_service.service.CartItemService;
import com.shopping.microservices.cart_service.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for cart operations
 */
@RestController
@RequestMapping("/api/v1/public/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartItemService cartItemService;

    /**
     * Add item to cart or update quantity if already exists
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> addItemToCart(
            @Valid @RequestBody CartItemRequestDto request) {
        
        String customerId = SecurityUtils.getCurrentCustomerId();
        CartItemResponseDto response = cartItemService.addOrUpdateCartItem(customerId, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Item added to cart successfully", response)
        );
    }

    /**
     * Update cart item quantity
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<CartItemResponseDto>> updateCartItemQuantity(
            @PathVariable Long productId,
            @Valid @RequestBody CartItemRequestDto request) {
        
        String customerId = SecurityUtils.getCurrentCustomerId();
        CartItemResponseDto response = cartItemService.updateCartItemQuantity(customerId, productId, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Cart item quantity updated successfully", response)
        );
    }

    /**
     * Get all cart items for authenticated customer
     */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<EnrichedCartItemResponseDto>>> getCartItems() {
        
        String customerId = SecurityUtils.getCurrentCustomerId();
        List<EnrichedCartItemResponseDto> items = cartItemService.getEnrichedCartItemsByCustomerId(customerId);
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Cart items retrieved successfully", items)
        );
    }

    /**
     * Remove single cart item
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Long productId) {
        
        String customerId = SecurityUtils.getCurrentCustomerId();
        cartItemService.removeCartItem(customerId, productId);
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Item removed from cart successfully", null)
        );
    }

    /**
     * Remove multiple cart items
     */
    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<Void>> removeMultipleCartItems(@RequestParam List<Long> productIds) {
        
        String customerId = SecurityUtils.getCurrentCustomerId();
        cartItemService.removeMultipleCartItems(customerId, productIds);
        
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK.value(), "Items removed from cart successfully", null)
        );
    }
}
