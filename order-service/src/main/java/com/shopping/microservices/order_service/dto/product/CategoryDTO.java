package com.shopping.microservices.order_service.dto.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoryDTO(Long id, String name, String description) implements Serializable {
}