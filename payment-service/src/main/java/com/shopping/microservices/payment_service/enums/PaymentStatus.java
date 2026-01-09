package com.shopping.microservices.payment_service.enums;

public enum PaymentStatus {
    PENDING,        // Đang chờ xử lý
    PROCESSING,     // Đang xử lý
    COMPLETED,      // Hoàn thành
    FAILED,         // Thất bại
    CANCELLED       // Đã hủy
}

