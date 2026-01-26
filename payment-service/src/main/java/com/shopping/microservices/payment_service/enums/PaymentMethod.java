package com.shopping.microservices.payment_service.enums;

public enum PaymentMethod {
    VNPAY("VNPay - Cổng thanh toán điện tử"),
    MOMO("MoMo - Ví điện tử"),
    PAYPAL("PayPal"),
    BANK_TRANSFER("Chuyển khoản ngân hàng"),
    CREDIT_CARD("Thẻ tín dụng/Ghi nợ"),
    ATM_CARD("Thẻ ATM nội địa"),
    COD("Thanh toán khi nhận hàng"),
    WALLET("Ví điện tử");
    
    private final String displayName;
    
    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static PaymentMethod fromString(String method) {
        try {
            return PaymentMethod.valueOf(method.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}

