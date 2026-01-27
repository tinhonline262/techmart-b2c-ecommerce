package com.shopping.microservices.payment_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitiateSimplePaymentRequest {

    private CustomerInfo customerInfo;

    @Builder.Default
    private String locale = "vi";

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
}
