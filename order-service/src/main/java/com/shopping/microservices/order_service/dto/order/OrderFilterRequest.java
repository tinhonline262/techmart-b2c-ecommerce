package com.shopping.microservices.order_service.dto.order;

import com.shopping.microservices.order_service.enumeration.OrderStatus;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record OrderFilterRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        String productName,

        OrderStatus orderStatus,

        String paymentStatus,

        String shipmentStatus,

        String customerId,

        String email
) {
}
