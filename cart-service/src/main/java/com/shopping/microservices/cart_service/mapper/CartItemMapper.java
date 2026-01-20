package com.shopping.microservices.cart_service.mapper;

import com.shopping.microservices.cart_service.dto.CartItemRequestDto;
import com.shopping.microservices.cart_service.dto.CartItemResponseDto;
import com.shopping.microservices.cart_service.entity.CartItem;
import com.shopping.microservices.cart_service.entity.CartItemId;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper utility class for converting between CartItem entity and DTOs
 */
public class CartItemMapper {

    private CartItemMapper() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert CartItemRequestDto to CartItem entity
     *
     * @param customerId customer ID from security context
     * @param dto request DTO
     * @return CartItem entity
     */
    public static CartItem toEntity(String customerId, CartItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        CartItem entity = new CartItem();
        
        CartItemId id = new CartItemId();
        id.setCustomerId(customerId);
        id.setProductId(dto.getProductId());
        
        entity.setId(id);
        entity.setQuantity(dto.getQuantity());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        
        return entity;
    }

    /**
     * Convert CartItem entity to CartItemResponseDto
     *
     * @param entity CartItem entity
     * @return response DTO
     */
    public static CartItemResponseDto toDto(CartItem entity) {
        if (entity == null) {
            return null;
        }

        return CartItemResponseDto.builder()
                .customerId(entity.getId().getCustomerId())
                .productId(entity.getId().getProductId())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert list of CartItem entities to list of CartItemResponseDto
     *
     * @param entities list of CartItem entities
     * @return list of response DTOs
     */
    public static List<CartItemResponseDto> toDtoList(List<CartItem> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(CartItemMapper::toDto)
                .collect(Collectors.toList());
    }
}
