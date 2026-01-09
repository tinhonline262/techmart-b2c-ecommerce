package com.shopping.microservices.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.enumeration.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for {@link Order}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreationDTO(@NotNull Long customerId,
                               @NotNull @Size(max = 255) String customerName,
                               @NotNull @Size(max = 255) @Email String customerEmail,
                               Set<OrderItemDTO> items, PaymentMethod paymentMethod) implements Serializable {
}