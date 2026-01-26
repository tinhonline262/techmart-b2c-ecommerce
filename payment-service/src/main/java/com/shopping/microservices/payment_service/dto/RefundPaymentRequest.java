package com.shopping.microservices.payment_service.dto;

import com.shopping.microservices.payment_service.enums.RefundType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundPaymentRequest {
    
    @NotNull
    private Long paymentId;
    
    @NotNull
    @Positive
    private BigDecimal refundAmount;
    
    @NotBlank
    @Size(max = 500)
    private String reason;
    
    private RefundType refundType;
}
