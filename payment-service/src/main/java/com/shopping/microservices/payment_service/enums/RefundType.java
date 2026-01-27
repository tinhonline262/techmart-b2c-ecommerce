package com.shopping.microservices.payment_service.enums;

public enum RefundType {
    FULL("Hoàn tiền toàn bộ"),
    PARTIAL("Hoàn tiền một phần");
    
    private final String displayName;
    
    RefundType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
