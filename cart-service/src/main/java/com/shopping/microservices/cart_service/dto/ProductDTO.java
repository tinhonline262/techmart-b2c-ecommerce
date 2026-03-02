package com.shopping.microservices.cart_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO representing product information fetched from Product Service.
 * Used to enrich cart items with product details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO implements Serializable {

    private Long id;

    private String name;

    private String slug;

    private BigDecimal price;

    private BigDecimal oldPrice;

    private BigDecimal specialPrice;

    private String thumbnailUrl;

    private String brandName;

    private Double averageRating;

    private Integer reviewCount;

    private boolean inStock;

}
