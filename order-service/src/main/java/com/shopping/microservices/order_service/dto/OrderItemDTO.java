package com.shopping.microservices.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.order_service.entity.OrderItem;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * DTO for {@link OrderItem}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderItemDTO(@NotNull String sku,
                           @NotNull @Positive Integer quantity) implements Serializable {
}