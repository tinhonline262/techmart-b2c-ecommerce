package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.product_service.entity.ProductImage;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link ProductImage}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductImageDTO(Long id, String imageUrl, Boolean isPrimary, Integer displayOrder,
                              Instant createdAt) implements Serializable {
}