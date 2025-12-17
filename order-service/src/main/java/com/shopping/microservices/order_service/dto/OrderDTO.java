package com.shopping.microservices.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.order_service.entity.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link Order}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderDTO(Long id, @NotNull @Size(max = 255) String orderNumber, @NotNull Long customerId, @NotNull @Size(max = 255) String customerName, @NotNull @Size(max = 255) String customerEmail, Instant orderDate, @NotNull @Size(max = 50) String status, @NotNull BigDecimal totalAmount) implements Serializable {

}