package com.shopping.microservices.order_service.dto.checkout;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record CreateCheckoutRequest(
        @Email(message = "Invalid email format")
        String email,

        @Size(max = 1000, message = "Note cannot exceed 1000 characters")
        String note,

        @Size(max = 100, message = "Promotion code cannot exceed 100 characters")
        String promotionCode,

        String shipmentMethodId,

        String paymentMethodId,

        Long shippingAddressId,

        @NotEmpty(message = "Checkout must have at least one item")
        @Valid
        List<CheckoutItemRequest> items
) {
}
