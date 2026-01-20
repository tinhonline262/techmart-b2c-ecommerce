package com.shopping.microservices.cart_service.service.impl;

import com.shopping.microservices.cart_service.dto.CartItemRequestDto;
import com.shopping.microservices.cart_service.dto.CartItemResponseDto;
import com.shopping.microservices.cart_service.entity.CartItem;
import com.shopping.microservices.cart_service.entity.CartItemId;
import com.shopping.microservices.cart_service.exception.CartItemNotFoundException;
import com.shopping.microservices.cart_service.exception.InvalidQuantityException;
import com.shopping.microservices.cart_service.mapper.CartItemMapper;
import com.shopping.microservices.cart_service.repository.CartItemRepository;
import com.shopping.microservices.cart_service.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CartItemService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    @CacheEvict(value = "customerCart", key = "#customerId")
    public CartItemResponseDto addOrUpdateCartItem(String customerId, CartItemRequestDto request) {
        log.debug("Adding or updating cart item for customer: {}, productId: {}", customerId, request.getProductId());

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            log.error("Invalid cart item request: {}", request);
            throw new InvalidQuantityException("Quantity must be greater than zero");

        }
        CartItemId cartItemId = new CartItemId();
        cartItemId.setCustomerId(customerId);
        cartItemId.setProductId(request.getProductId());

        Optional<CartItem> existingItem = cartItemRepository.findById(cartItemId);
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setUpdatedAt(Instant.now());
            log.debug("Updating existing cart item. New quantity: {}", cartItem.getQuantity());
        } else {
            cartItem = CartItemMapper.toEntity(customerId, request);
            log.debug("Creating new cart item with quantity: {}", cartItem.getQuantity());
        }

        CartItem savedItem = cartItemRepository.save(cartItem);
        log.info("Cart item saved successfully for customer: {}, productId: {}", customerId, request.getProductId());

        return CartItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    @CacheEvict(value = "customerCart", key = "#customerId")
    public CartItemResponseDto updateCartItemQuantity(String customerId, Long productId, CartItemRequestDto request) {
        log.debug("Updating cart item quantity for customer: {}, productId: {}", customerId, productId);

        CartItemId cartItemId = new CartItemId();
        cartItemId.setCustomerId(customerId);
        cartItemId.setProductId(productId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(customerId, productId));

        cartItem.setQuantity(request.getQuantity());
        cartItem.setUpdatedAt(Instant.now());

        CartItem savedItem = cartItemRepository.save(cartItem);
        log.info("Cart item quantity updated successfully for customer: {}, productId: {}", customerId, productId);

        return CartItemMapper.toDto(savedItem);
    }

    @Override
    @Cacheable(value = "customerCart", key = "#customerId")
    public List<CartItemResponseDto> getCartItemsByCustomerId(String customerId) {
        log.debug("Retrieving cart items for customer: {}", customerId);

        List<CartItem> cartItems = cartItemRepository.findByIdCustomerId(customerId);
        log.info("Retrieved {} cart items for customer: {}", cartItems.size(), customerId);

        return CartItemMapper.toDtoList(cartItems);
    }

    @Override
    @Transactional
    @CacheEvict(value = "customerCart", key = "#customerId")
    public void removeCartItem(String customerId, Long productId) {
        log.debug("Removing cart item for customer: {}, productId: {}", customerId, productId);
        if (!cartItemRepository.existsByIdCustomerIdAndIdProductId(customerId, productId)) {
            throw new CartItemNotFoundException(customerId, productId);
        }
        cartItemRepository.deleteByIdCustomerIdAndIdProductId(customerId, productId);
        log.info("Cart item removed successfully for customer: {}, productId: {}", customerId, productId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "customerCart", key = "#customerId")
    public void removeMultipleCartItems(String customerId, List<Long> productIds) {
        log.debug("Removing {} cart items for customer: {}", productIds.size(), customerId);
        if (!productIds.stream().allMatch(pid -> cartItemRepository.existsByIdCustomerIdAndIdProductId(customerId, pid))) {
            throw new CartItemNotFoundException(customerId, null);
        }
        cartItemRepository.deleteByIdCustomerIdAndIdProductIdIn(customerId, productIds);
        log.info("Multiple cart items removed successfully for customer: {}", customerId);
    }
}
