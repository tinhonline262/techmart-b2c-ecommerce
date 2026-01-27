package com.shopping.microservices.payment_service.kafka.producer;

import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.payment_service.entity.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Publisher for payment-related events.
 * 
 * Encapsulates event creation and publishing logic for payment domain events.
 * Uses the common EventPublisher for actual Kafka operations.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final EventPublisher eventPublisher;
    private static final String SOURCE = "payment-service";

    /**
     * Publish PAYMENT_INITIATED event
     */
    public void publishPaymentInitiated(Payment payment) {
        PaymentEvent event = PaymentEvent.paymentInitiated(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getPaymentMethod().name()
        );
        event.setCorrelationId(payment.getCheckoutId());
        
        log.info("Publishing PAYMENT_INITIATED event: paymentId={}, orderId={}", 
            payment.getId(), payment.getOrderId());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    /**
     * Publish PAYMENT_SUCCESS event
     */
    public void publishPaymentSuccess(Payment payment) {
        PaymentEvent event = PaymentEvent.paymentSuccess(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getGatewayTransactionId()
        );
        event.setCorrelationId(payment.getCheckoutId());
        event.setPaymentMethod(payment.getPaymentMethod().name());
        event.setPaymentProvider(getProviderForMethod(payment.getPaymentMethod().name()));
        
        log.info("Publishing PAYMENT_SUCCESS event: paymentId={}, orderId={}, transactionId={}", 
            payment.getId(), payment.getOrderId(), payment.getGatewayTransactionId());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    /**
     * Publish PAYMENT_FAILED event
     */
    public void publishPaymentFailed(Payment payment, String failureReason) {
        PaymentEvent event = PaymentEvent.paymentFailed(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            failureReason
        );
        event.setCorrelationId(payment.getCheckoutId());
        event.setPaymentMethod(payment.getPaymentMethod().name());
        
        log.info("Publishing PAYMENT_FAILED event: paymentId={}, orderId={}, reason={}", 
            payment.getId(), payment.getOrderId(), failureReason);
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    /**
     * Publish PAYMENT_REFUNDED event
     */
    public void publishPaymentRefunded(Payment payment, BigDecimal refundAmount, String reason) {
        PaymentEvent event = PaymentEvent.paymentRefunded(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            refundAmount,
            reason
        );
        event.setCorrelationId(payment.getCheckoutId());
        event.setPaymentMethod(payment.getPaymentMethod().name());
        
        log.info("Publishing PAYMENT_REFUNDED event: paymentId={}, orderId={}, amount={}", 
            payment.getId(), payment.getOrderId(), refundAmount);
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    /**
     * Publish PAYMENT_TIMEOUT event
     */
    public void publishPaymentTimeout(Payment payment) {
        PaymentEvent event = new PaymentEvent(PaymentEvent.PaymentEventType.PAYMENT_TIMEOUT, SOURCE);
        event.setPaymentId(payment.getId());
        event.setOrderId(payment.getOrderId());
        event.setCorrelationId(payment.getCheckoutId());
        event.setFailureReason("Payment expired");
        event.setPaymentMethod(payment.getPaymentMethod().name());
        
        log.info("Publishing PAYMENT_TIMEOUT event: paymentId={}, orderId={}", 
            payment.getId(), payment.getOrderId());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    /**
     * Publish notification event for payment success
     */
    public void publishPaymentSuccessNotification(Payment payment, String customerEmail, String customerName) {
        if (customerEmail == null || customerEmail.isBlank()) {
            log.debug("Skipping notification - no customer email for payment: {}", payment.getId());
            return;
        }

        NotificationEvent event = NotificationEvent.builder()
            .recipient(customerEmail)
            .template(NotificationEvent.NotificationTemplate.PAYMENT_SUCCESS)
            .subject("Thanh toán thành công - Đơn hàng #" + payment.getOrderId())
            .customerName(customerName)
            .priority(NotificationEvent.NotificationPriority.HIGH)
            .data(Map.of(
                "paymentId", payment.getId(),
                "orderId", payment.getOrderId(),
                "amount", payment.getAmount(),
                "paymentMethod", payment.getPaymentMethod().getDisplayName(),
                "transactionId", payment.getGatewayTransactionId() != null ? payment.getGatewayTransactionId() : ""
            ))
            .build();
        event.setEventType(NotificationEvent.NotificationEventType.EMAIL_SEND.name());
        event.setSource(SOURCE);
        event.setCorrelationId(payment.getCheckoutId());

        log.info("Publishing payment success notification: paymentId={}, email={}", 
            payment.getId(), customerEmail);
        eventPublisher.publish(KafkaTopics.NOTIFICATION_EVENTS, event);
    }

    /**
     * Publish notification event for payment failure
     */
    public void publishPaymentFailedNotification(Payment payment, String customerEmail, String customerName, String reason) {
        if (customerEmail == null || customerEmail.isBlank()) {
            log.debug("Skipping notification - no customer email for payment: {}", payment.getId());
            return;
        }

        NotificationEvent event = NotificationEvent.builder()
            .recipient(customerEmail)
            .template(NotificationEvent.NotificationTemplate.PAYMENT_FAILED)
            .subject("Thanh toán thất bại - Đơn hàng #" + payment.getOrderId())
            .customerName(customerName)
            .priority(NotificationEvent.NotificationPriority.HIGH)
            .data(Map.of(
                "paymentId", payment.getId(),
                "orderId", payment.getOrderId(),
                "amount", payment.getAmount(),
                "paymentMethod", payment.getPaymentMethod().getDisplayName(),
                "failureReason", reason != null ? reason : "Không xác định"
            ))
            .build();
        event.setEventType(NotificationEvent.NotificationEventType.EMAIL_SEND.name());
        event.setSource(SOURCE);
        event.setCorrelationId(payment.getCheckoutId());

        log.info("Publishing payment failed notification: paymentId={}, email={}", 
            payment.getId(), customerEmail);
        eventPublisher.publish(KafkaTopics.NOTIFICATION_EVENTS, event);
    }

    /**
     * Publish notification event for refund
     */
    public void publishRefundNotification(Payment payment, BigDecimal refundAmount, String customerEmail, String customerName) {
        if (customerEmail == null || customerEmail.isBlank()) {
            log.debug("Skipping notification - no customer email for payment: {}", payment.getId());
            return;
        }

        NotificationEvent event = NotificationEvent.builder()
            .recipient(customerEmail)
            .template(NotificationEvent.NotificationTemplate.PAYMENT_REFUNDED)
            .subject("Hoàn tiền thành công - Đơn hàng #" + payment.getOrderId())
            .customerName(customerName)
            .priority(NotificationEvent.NotificationPriority.MEDIUM)
            .data(Map.of(
                "paymentId", payment.getId(),
                "orderId", payment.getOrderId(),
                "refundAmount", refundAmount,
                "paymentMethod", payment.getPaymentMethod().getDisplayName()
            ))
            .build();
        event.setEventType(NotificationEvent.NotificationEventType.EMAIL_SEND.name());
        event.setSource(SOURCE);
        event.setCorrelationId(payment.getCheckoutId());

        log.info("Publishing refund notification: paymentId={}, email={}", 
            payment.getId(), customerEmail);
        eventPublisher.publish(KafkaTopics.NOTIFICATION_EVENTS, event);
    }

    private String getProviderForMethod(String paymentMethod) {
        return switch (paymentMethod) {
            case "VNPAY", "BANK_TRANSFER", "ATM_CARD" -> "VNPAY";
            case "MOMO", "WALLET" -> "MOMO";
            case "PAYPAL" -> "PAYPAL";
            case "COD" -> "COD";
            case "CREDIT_CARD" -> "VNPAY"; // Default credit card provider
            default -> paymentMethod;
        };
    }
}
