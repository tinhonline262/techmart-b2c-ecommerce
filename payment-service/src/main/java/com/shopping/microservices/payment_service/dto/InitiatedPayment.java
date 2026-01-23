package com.shopping.microservices.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InitiatedPayment {
    
    @NotBlank
    private String status; // "success", "failed", "pending"
    
    private String paymentId; // Internal payment ID
    
    private String redirectUrl; // URL to redirect customer for payment
    
    private String qrCode; // QR code data for scanning (optional)
    
    private String deepLink; // Mobile app deep link (optional)
    
    private LocalDateTime expiresAt; // Payment link expiration
    
    private Map<String, Object> additionalData; // Provider-specific data
    
    // Business methods
    public boolean isSuccessful() {
        return "success".equalsIgnoreCase(status);
    }
    
    public boolean requiresRedirect() {
        return redirectUrl != null && !redirectUrl.isEmpty();
    }
    
    public boolean hasQrCode() {
        return qrCode != null && !qrCode.isEmpty();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
