package com.shopping.microservices.cart_service.service;

import com.shopping.microservices.cart_service.dto.CartItemRequestDto;
import com.shopping.microservices.cart_service.dto.CartItemResponseDto;

import java.util.List;

/**
 * Service interface for cart item operations
 */
public interface CartItemService {

    /**
     * Add or update a cart item for a customer
     * If the item already exists, update the quantity
     *
     * @param customerId customer ID
     * @param request cart item request DTO
     * @return cart item response DTO
     */
    CartItemResponseDto addOrUpdateCartItem(String customerId, CartItemRequestDto request);

    /**
     * Update the quantity of an existing cart item
     *
     * @param customerId customer ID
     * @param productId product ID
     * @param request cart item request DTO
     * @return updated cart item response DTO
     */
    CartItemResponseDto updateCartItemQuantity(String customerId, Long productId, CartItemRequestDto request);

    /**
     * Get all cart items for a customer
     *
     * @param customerId customer ID
     * @return list of cart item response DTOs
     */
    List<CartItemResponseDto> getCartItemsByCustomerId(String customerId);

    /**
     * Remove a specific cart item
     *
     * @param customerId customer ID
     * @param productId product ID
     */
    void removeCartItem(String customerId, Long productId);

    /**
     * Remove multiple cart items
     *
     * @param customerId customer ID
     * @param productIds list of product IDs
     */
    void removeMultipleCartItems(String customerId, List<Long> productIds);
}
