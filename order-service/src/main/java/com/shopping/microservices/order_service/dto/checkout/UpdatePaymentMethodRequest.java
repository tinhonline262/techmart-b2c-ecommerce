package com.shopping.microservices.order_service.dto.checkout;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdatePaymentMethodRequest(
        @NotBlank(message = "Payment method ID is required")
        @Size(max = 50, message = "Payment method ID cannot exceed 50 characters")
        String paymentMethodId
) {
}
