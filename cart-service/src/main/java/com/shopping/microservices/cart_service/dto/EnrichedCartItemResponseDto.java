package com.shopping.microservices.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for cart item responses enriched with product information.
 * Combines cart data with product details from Product Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrichedCartItemResponseDto implements Serializable {

    // Cart data
    private String customerId;
    
    private Long productId;
    
    private Integer quantity;
    
    private Instant createdAt;
    
    private Instant updatedAt;

    // Product data (from Product Service)
    private String productName;
    
    private String productSlug;
    
    private BigDecimal productPrice;
    
    private BigDecimal oldPrice;
    
    private BigDecimal specialPrice;
    
    private String thumbnailUrl;
    
    private String brandName;
    
    private boolean inStock;
    
    private boolean hasDiscount;
    
    private Double averageRating;
    
    private Integer reviewCount;

    // Calculated fields
    private BigDecimal itemTotal;
    
    private BigDecimal itemDiscount;
    
    /**
     * Flag indicating if product information was successfully fetched.
     * If false, only cart data is available.
     */
    private boolean productInfoAvailable;
    
    /**
     * Creates an enriched cart item from cart data and product data.
     *
     * @param cartItem the cart item data
     * @param product  the product data (can be null if product not found)
     * @return EnrichedCartItemResponseDto with combined data
     */
    public static EnrichedCartItemResponseDto fromCartAndProduct(
            CartItemResponseDto cartItem, 
            ProductDTO product) {
        
        EnrichedCartItemResponseDtoBuilder builder = EnrichedCartItemResponseDto.builder()
                .customerId(cartItem.getCustomerId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt());
        
        if (product != null) {
            BigDecimal effectivePrice = product.getSpecialPrice() != null 
                    ? product.getSpecialPrice() 
                    : product.getPrice();
            BigDecimal itemTotal = effectivePrice != null 
                    ? effectivePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    : BigDecimal.ZERO;
            BigDecimal itemDiscount = BigDecimal.ZERO;
            
            if (product.getPrice() != null && product.getSpecialPrice() != null) {
                BigDecimal originalTotal = product.getPrice()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                itemDiscount = originalTotal.subtract(itemTotal);
            }
            
            builder.productName(product.getName())
                    .productSlug(product.getSlug())
                    .productPrice(product.getPrice())
                    .oldPrice(product.getOldPrice())
                    .specialPrice(product.getSpecialPrice())
                    .thumbnailUrl(product.getThumbnailUrl())
                    .brandName(product.getBrandName())
                    .inStock(product.isInStock())
                    .averageRating(product.getAverageRating())
                    .reviewCount(product.getReviewCount())
                    .itemTotal(itemTotal)
                    .itemDiscount(itemDiscount)
                    .productInfoAvailable(true);
        } else {
            builder.productInfoAvailable(false)
                    .itemTotal(BigDecimal.ZERO)
                    .itemDiscount(BigDecimal.ZERO);
        }
        
        return builder.build();
    }
}
