package com.shopping.microservices.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO for cart item responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {

    private String customerId;
    
    private Long productId;
    
    private Integer quantity;
    
    private Instant createdAt;
    
    private Instant updatedAt;
}
