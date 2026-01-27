package com.shopping.microservices.payment_service.enums;

public enum PaymentStatus {
    PENDING("Chờ thanh toán"),
    INITIATED("Đã khởi tạo"),
    PROCESSING("Đang xử lý"),
    SUCCESS("Thành công"),
    FAILED("Thất bại"),
    CANCELLED("Đã hủy"),
    REFUNDED("Đã hoàn tiền"),
    PARTIALLY_REFUNDED("Hoàn tiền một phần"),
    EXPIRED("Hết hạn");
    
    private final String displayName;
    
    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isTerminal() {
        return this == SUCCESS || this == FAILED || 
               this == CANCELLED || this == REFUNDED || this == EXPIRED;
    }
    
    public boolean canRefund() {
        return this == SUCCESS || this == PARTIALLY_REFUNDED;
    }
}

