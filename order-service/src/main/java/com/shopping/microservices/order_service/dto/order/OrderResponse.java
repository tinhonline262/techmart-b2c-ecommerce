package com.shopping.microservices.order_service.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
public record OrderResponse(
        Long orderId,
        String email,
        String note,
        String promotionCode,
        Integer numberItem,
        BigDecimal totalAmount,
        BigDecimal totalShipmentFee,
        BigDecimal totalShipmentTax,
        Float totalTax,
        Float totalDiscountAmount,
        String status,
        String shipmentMethodId,
        String shipmentStatus,
        String paymentStatus,
        Long paymentId,
        String checkoutId,
        String paymentMethodId,
        String progress,
        String customerId,
        String rejectReason,
        List<OrderItemResponse> items,
        OrderAddressResponse shippingAddress,
        Map<String, Object> attributes,
        Map<String, Object> lastError,
        Instant createdAt,
        Instant updatedAt
) {
}
