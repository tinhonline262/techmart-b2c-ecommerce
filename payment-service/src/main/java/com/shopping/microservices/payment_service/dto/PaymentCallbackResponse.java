package com.shopping.microservices.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response returned to payment provider after processing callback
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackResponse {
    
    /**
     * Whether the callback was processed successfully
     */
    private boolean success;
    
    /**
     * Response code to return to provider
     */
    private String responseCode;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Updated payment status
     */
    private String paymentStatus;
    
    /**
     * Payment ID that was processed
     */
    private Long paymentId;
    
    /**
     * Order ID associated with the payment
     */
    private Long orderId;

    public static PaymentCallbackResponse success(Long paymentId, Long orderId, String status) {
        return PaymentCallbackResponse.builder()
            .success(true)
            .responseCode("00")
            .message("Success")
            .paymentStatus(status)
            .paymentId(paymentId)
            .orderId(orderId)
            .build();
    }

    public static PaymentCallbackResponse error(String code, String message) {
        return PaymentCallbackResponse.builder()
            .success(false)
            .responseCode(code)
            .message(message)
            .build();
    }
}
