package com.shopping.microservices.order_service.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateOrderPaymentStatusRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotBlank(message = "Payment status is required")
        @Size(max = 50, message = "Payment status cannot exceed 50 characters")
        String paymentStatus,

        Long paymentId,

        @Size(max = 255, message = "Reject reason cannot exceed 255 characters")
        String rejectReason
) {
}
