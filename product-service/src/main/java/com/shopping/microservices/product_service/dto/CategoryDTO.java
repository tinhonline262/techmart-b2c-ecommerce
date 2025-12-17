package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.product_service.entity.Category;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link Category}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDTO(Long id, String name, String description) implements Serializable {
}