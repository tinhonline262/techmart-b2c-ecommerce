package com.shopping.microservices.order_service.dto.checkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateCheckoutStatusRequest(
        @NotBlank(message = "Checkout ID is required")
        String checkoutId,

        @NotBlank(message = "Status is required")
        @Size(max = 50, message = "Status cannot exceed 50 characters")
        String status,

        @Size(max = 50, message = "Progress cannot exceed 50 characters")
        String progress
) {
}
