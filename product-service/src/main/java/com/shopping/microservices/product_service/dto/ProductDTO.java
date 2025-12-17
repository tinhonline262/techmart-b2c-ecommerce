package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.product_service.entity.Product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link Product}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDTO(Long id, String name, String description, String sku, BigDecimal price,
                         Integer quantityInStock, CategoryDTO category, String brand,
                         Set<ProductImageDTO> productImages, Instant createdAt, Instant updatedAt) implements Serializable {
}