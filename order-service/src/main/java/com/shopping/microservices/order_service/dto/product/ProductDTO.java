package com.shopping.microservices.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductDTO(Long id, String name, String description, String sku, BigDecimal price,
                         Integer quantityInStock, CategoryDTO category, String brand,
                         Set<ProductImageDTO> productImages, Instant createdAt, Instant updatedAt) implements Serializable {
}