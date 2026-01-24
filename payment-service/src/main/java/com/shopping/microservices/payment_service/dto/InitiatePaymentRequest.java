package com.shopping.microservices.payment_service.dto;

import com.shopping.microservices.payment_service.enums.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiatePaymentRequest {
    
    private Long orderId;
    
    @NotBlank
    private String checkoutId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @NotNull
    private PaymentMethod paymentMethod;
    
    @URL
    private String cancelUrl;
    
    @URL
    private String callbackUrl;
    
    private CustomerInfo customerInfo;
    
    private List<PaymentItem> items; // For some providers
    
    @Builder.Default
    private String locale = "vi";
    
    private Map<String, Object> metadata;
    
    // Nested class for customer information
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String customerId;
        
        @Email
        private String email;
        
        private String phone;
        
        private String fullName;
        
        private String ipAddress;
    }
    
    // Nested class for payment items
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentItem {
        private String name;
        
        private Integer quantity;
        
        private BigDecimal price;
    }
}
