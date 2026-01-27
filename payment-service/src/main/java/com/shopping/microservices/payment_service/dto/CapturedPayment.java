package com.shopping.microservices.payment_service.dto;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapturedPayment {
    
    private Long orderId;
    
    private String checkoutId;
    
    @NotNull
    @Positive
    private BigDecimal amount;
    
    @Builder.Default
    private BigDecimal paymentFee = BigDecimal.ZERO;
    
    private String gatewayTransactionId;
    
    @NotNull
    private PaymentMethod paymentMethod;
    
    @NotNull
    private PaymentStatus paymentStatus;
    
    private String failureMessage;
    
    private Map<String, Object> metadata;
    
    // Business methods
    public boolean isSuccess() {
        return paymentStatus == PaymentStatus.SUCCESS;
    }
    
    public boolean isFailed() {
        return paymentStatus == PaymentStatus.FAILED;
    }
    
    public BigDecimal getTotalAmount() {
        return amount.add(paymentFee != null ? paymentFee : BigDecimal.ZERO);
    }
    
    // Convert to Payment entity
    public Payment toEntity() {
        return Payment.builder()
            .orderId(orderId)
            .checkoutId(checkoutId)
            .amount(amount)
            .paymentFee(paymentFee)
            .paymentMethod(paymentMethod)
            .paymentStatus(paymentStatus)
            .gatewayTransactionId(gatewayTransactionId)
            .failureMessage(failureMessage)
            .build();
    }
}
