package com.shopping.microservices.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductImageDTO(Long id, String imageUrl, Boolean isPrimary, Integer displayOrder,
                              Instant createdAt) implements Serializable {
}