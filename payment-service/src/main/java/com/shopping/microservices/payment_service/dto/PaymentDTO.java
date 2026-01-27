package com.shopping.microservices.payment_service.dto;

import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    
    private Long id;
    
    private Long orderId;
    
    private String checkoutId;
    
    private BigDecimal amount;
    
    private BigDecimal paymentFee;
    
    private PaymentMethod paymentMethod;
    
    private PaymentStatus paymentStatus;
    
    private String gatewayTransactionId;
    
    private String failureMessage;
    
    private String paymentProviderCheckoutId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    private String updatedBy;
    
    // Calculated fields
    private String providerName;
    
    private boolean canRefund; // calculated
    
    private BigDecimal totalAmount; // calculated
    
    private String statusDisplay; // localized status
}
