package com.shopping.microservices.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {
    
    private String refundId;
    
    private String status;
    
    private BigDecimal refundedAmount;
    
    private LocalDateTime refundedAt;
    
    private LocalDate estimatedCompletionDate;
}
