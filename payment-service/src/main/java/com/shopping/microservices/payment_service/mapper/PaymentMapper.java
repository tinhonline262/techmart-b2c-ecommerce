package com.shopping.microservices.payment_service.mapper;

import com.shopping.microservices.payment_service.dto.PaymentDTO;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.gateway.PaymentGatewayFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Payment entity and DTO
 */
@Component
public class PaymentMapper {

    private final PaymentGatewayFactory gatewayFactory;

    public PaymentMapper(PaymentGatewayFactory gatewayFactory) {
        this.gatewayFactory = gatewayFactory;
    }

    public PaymentDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        String providerName = null;
        try {
            String providerId = gatewayFactory.getProviderIdForMethod(payment.getPaymentMethod());
            providerName = providerId;
        } catch (Exception ignored) {}

        return PaymentDTO.builder()
            .id(payment.getId())
            .orderId(payment.getOrderId())
            .checkoutId(payment.getCheckoutId())
            .amount(payment.getAmount())
            .paymentFee(payment.getPaymentFee())
            .paymentMethod(payment.getPaymentMethod())
            .paymentStatus(payment.getPaymentStatus())
            .gatewayTransactionId(payment.getGatewayTransactionId())
            .failureMessage(payment.getFailureMessage())
            .paymentProviderCheckoutId(payment.getPaymentProviderCheckoutId())
            .createdAt(payment.getCreatedAt())
            .updatedAt(payment.getUpdatedAt())
            .createdBy(payment.getCreatedBy())
            .updatedBy(payment.getUpdatedBy())
            .providerName(providerName)
            .canRefund(payment.canRefund())
            .totalAmount(payment.getTotalAmount())
            .statusDisplay(payment.getPaymentStatus().getDisplayName())
            .build();
    }

    public Payment toEntity(PaymentDTO dto) {
        if (dto == null) {
            return null;
        }

        return Payment.builder()
            .id(dto.getId())
            .orderId(dto.getOrderId())
            .checkoutId(dto.getCheckoutId())
            .amount(dto.getAmount())
            .paymentFee(dto.getPaymentFee())
            .paymentMethod(dto.getPaymentMethod())
            .paymentStatus(dto.getPaymentStatus())
            .gatewayTransactionId(dto.getGatewayTransactionId())
            .failureMessage(dto.getFailureMessage())
            .paymentProviderCheckoutId(dto.getPaymentProviderCheckoutId())
            .build();
    }
}
