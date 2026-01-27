package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for ProductImage response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductImageDTO(
        Long id,
        Long productId,
        String imageUrl,
        String cloudinaryPublicId,
        String altText,
        boolean isPrimary,
        Integer displayOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}