package com.shopping.microservices.payment_service.entity;

import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_checkout_id", columnList = "checkout_id"),
    @Index(name = "idx_payment_status", columnList = "payment_status"),
    @Index(name = "idx_gateway_transaction_id", columnList = "gateway_transaction_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends AbstractAuditEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_id")
    private Long orderId;
    
    @Column(name = "checkout_id", length = 50)
    private String checkoutId;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "payment_fee", precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal paymentFee = BigDecimal.ZERO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 50)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "gateway_transaction_id", length = 255)
    private String gatewayTransactionId;
    
    @Column(name = "failure_message", length = 1000)
    private String failureMessage;
    
    @Column(name = "payment_provider_checkout_id", length = 255)
    private String paymentProviderCheckoutId;
    
    // Business methods
    public boolean isPending() {
        return paymentStatus == PaymentStatus.PENDING || 
               paymentStatus == PaymentStatus.INITIATED;
    }
    
    public boolean isSuccessful() {
        return paymentStatus == PaymentStatus.SUCCESS;
    }
    
    public boolean isFailed() {
        return paymentStatus == PaymentStatus.FAILED;
    }
    
    public boolean canRefund() {
        return paymentStatus.canRefund();
    }
    
    public BigDecimal getTotalAmount() {
        return amount.add(paymentFee != null ? paymentFee : BigDecimal.ZERO);
    }
    
    public void markAsSuccess(String transactionId) {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.gatewayTransactionId = transactionId;
        this.failureMessage = null;
    }
    
    public void markAsFailed(String failureMessage) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.failureMessage = failureMessage;
    }
    
    public void markAsRefunded() {
        this.paymentStatus = PaymentStatus.REFUNDED;
    }
}