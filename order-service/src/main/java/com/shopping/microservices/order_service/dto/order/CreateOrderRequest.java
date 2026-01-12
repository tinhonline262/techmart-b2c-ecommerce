package com.shopping.microservices.order_service.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CreateOrderRequest(
        @NotBlank(message = "Checkout ID is required")
        String checkoutId,

        @Email(message = "Invalid email format")
        String email,

        @Size(max = 1000, message = "Note cannot exceed 1000 characters")
        String note,

        @Size(max = 100, message = "Promotion code cannot exceed 100 characters")
        String promotionCode,

        String customerId,

        String shipmentMethodId,

        String paymentMethodId,

        @Valid
        OrderAddressRequest shippingAddress,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items,

        Map<String, Object> attributes
) {
}
