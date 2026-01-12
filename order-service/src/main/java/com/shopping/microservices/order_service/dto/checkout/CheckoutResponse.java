package com.shopping.microservices.order_service.dto.checkout;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
public record CheckoutResponse(
        String id,
        String email,
        String note,
        String promotionCode,
        String status,
        String progress,
        String customerId,
        String shipmentMethodId,
        String paymentMethodId,
        Long shippingAddressId,
        BigDecimal totalAmount,
        BigDecimal totalShipmentFee,
        BigDecimal totalShipmentTax,
        BigDecimal totalTax,
        BigDecimal totalDiscountAmount,
        List<CheckoutItemResponse> items,
        Map<String, Object> attributes,
        Map<String, Object> lastError,
        Instant createdAt,
        Instant updatedAt
) {
}
