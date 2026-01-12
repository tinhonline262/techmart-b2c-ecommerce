package com.shopping.microservices.order_service.dto.order;

import com.shopping.microservices.order_service.enumeration.OrderStatus;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ExportOrderCsvRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate,

        String productName,

        OrderStatus orderStatus,

        String paymentStatus,

        String shipmentStatus,

        List<Long> orderIds,

        List<String> columns
) {
}
