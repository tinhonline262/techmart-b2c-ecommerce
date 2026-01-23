package com.shopping.microservices.payment_service.service.impl;

import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.common_library.exception.PaymentException;
import com.shopping.microservices.common_library.kafka.EventPublisher;
import com.shopping.microservices.payment_service.dto.*;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import com.shopping.microservices.payment_service.gateway.PaymentGateway;
import com.shopping.microservices.payment_service.gateway.PaymentGatewayFactory;
import com.shopping.microservices.payment_service.mapper.PaymentMapper;
import com.shopping.microservices.payment_service.repository.PaymentRepository;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Payment Service implementation handling core payment business logic.
 * 
 * Uses the PaymentGatewayFactory to delegate to appropriate payment providers.
 * Publishes events to Kafka for cross-service communication.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentGatewayFactory gatewayFactory;
    private final PaymentMapper paymentMapper;
    private final EventPublisher eventPublisher;

    private static final String SOURCE = "payment-service";

    @Value("${payment.expiry.minutes:30}")
    private int paymentExpiryMinutes;

    // ==================== Create Payment ====================

    @Override
    @Transactional
    public PaymentDTO createPayment(String checkoutId, Long orderId, BigDecimal amount, PaymentMethod paymentMethod) {
        log.info("Creating payment for checkoutId: {}, orderId: {}, amount: {}, method: {}",
            checkoutId, orderId, amount, paymentMethod);

        // Check if payment already exists for this checkout
        Optional<Payment> existingPayment = paymentRepository.findByCheckoutId(checkoutId);
        if (existingPayment.isPresent()) {
            Payment existing = existingPayment.get();
            if (existing.isPending()) {
                log.info("Returning existing pending payment for checkout: {}", checkoutId);
                return paymentMapper.toDTO(existing);
            }
            // If existing payment is in terminal state (failed/cancelled), allow creating new one
            if (!existing.getPaymentStatus().isTerminal()) {
                throw new PaymentException(existing.getId(), orderId, 
                    "Payment already exists for checkout: " + checkoutId, null, null);
            }
        }

        // Create new payment
        Payment payment = Payment.builder()
            .checkoutId(checkoutId)
            .orderId(orderId)
            .amount(amount)
            .paymentFee(BigDecimal.ZERO)
            .paymentMethod(paymentMethod)
            .paymentStatus(PaymentStatus.PENDING)
            .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {} for checkout: {}", savedPayment.getId(), checkoutId);

        return paymentMapper.toDTO(savedPayment);
    }

    // ==================== Initiate Payment ====================

    @Override
    @Transactional
    public InitiatedPayment initiatePayment(Long paymentId, InitiatePaymentRequest request) {
        log.info("Initiating payment: {}", paymentId);

        // Load payment with pessimistic lock to prevent concurrent initiation
        Payment payment = paymentRepository.findByIdWithLock(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        // Validate payment status
        if (!payment.isPending()) {
            throw new PaymentException(paymentId, 
                "Payment is not in pending status: " + payment.getPaymentStatus());
        }

        // Get appropriate gateway
        String providerId = gatewayFactory.getProviderIdForMethod(payment.getPaymentMethod());
        PaymentGateway gateway = gatewayFactory.getGatewayById(providerId);

        // Validate gateway supports payment method
        if (!gateway.supports(payment.getPaymentMethod())) {
            throw new PaymentException(payment.getId(), 
                "Gateway " + providerId + " does not support payment method: " + payment.getPaymentMethod());
        }

        // Initiate payment with gateway
        InitiatedPayment initiatedPayment = gateway.initiatePayment(payment, request);

        if (initiatedPayment.isSuccessful()) {
            // Update payment status
            payment.setPaymentStatus(PaymentStatus.INITIATED);
            payment.setPaymentProviderCheckoutId(
                initiatedPayment.getAdditionalData() != null ? 
                    String.valueOf(initiatedPayment.getAdditionalData().get("requestId")) : null
            );
            paymentRepository.save(payment);

            // Publish PAYMENT_INITIATED event
            publishPaymentInitiatedEvent(payment);

            log.info("Payment initiated successfully: {}, redirectUrl: {}", 
                paymentId, initiatedPayment.getRedirectUrl());
        } else {
            log.error("Failed to initiate payment: {}", paymentId);
        }

        return initiatedPayment;
    }

    // ==================== Handle Callback ====================

    @Override
    @Transactional
    public PaymentCallbackResponse handleCallback(String providerId, Map<String, String> params) {
        log.info("Handling callback from provider: {}", providerId);

        try {
            // Get gateway
            PaymentGateway gateway = gatewayFactory.getGatewayById(providerId);

            // Verify callback signature
            if (!gateway.verifyCallback(params)) {
                log.error("Callback signature verification failed for provider: {}", providerId);
                return PaymentCallbackResponse.error("01", "Invalid signature");
            }

            // Process callback
            CapturedPayment capturedPayment = gateway.processCallback(params);

            // Find payment by transaction ID or order ID from params
            String txnRef = params.get("vnp_TxnRef"); // VNPay
            String orderId = params.get("orderId"); // MoMo, PayPal
            
            Payment payment = findPaymentFromCallback(txnRef, orderId);
            if (payment == null) {
                log.error("Payment not found for callback, txnRef: {}, orderId: {}", txnRef, orderId);
                return PaymentCallbackResponse.error("02", "Payment not found");
            }

            // Lock payment for update
            Long pid = payment.getId();
            payment = paymentRepository.findByIdWithLock(pid)
                .orElseThrow(() -> new PaymentException(pid, "Payment not found: " + pid));

            // Check if already processed (idempotency)
            if (payment.getPaymentStatus().isTerminal()) {
                log.info("Payment already in terminal state: {}, status: {}", 
                    payment.getId(), payment.getPaymentStatus());
                return PaymentCallbackResponse.success(
                    payment.getId(), 
                    payment.getOrderId(), 
                    payment.getPaymentStatus().name()
                );
            }

            // Update payment based on callback result
            if (capturedPayment.isSuccess()) {
                payment.markAsSuccess(capturedPayment.getGatewayTransactionId());
                paymentRepository.save(payment);
                
                // Publish success event
                publishPaymentSuccessEvent(payment);
                
                // Send notification
                publishPaymentNotification(payment, NotificationEvent.NotificationTemplate.PAYMENT_SUCCESS);

                log.info("Payment successful: {}, transactionId: {}", 
                    payment.getId(), capturedPayment.getGatewayTransactionId());
                
            } else {
                payment.markAsFailed(capturedPayment.getFailureMessage());
                paymentRepository.save(payment);
                
                // Publish failure event
                publishPaymentFailedEvent(payment, capturedPayment.getFailureMessage());
                
                // Send notification
                publishPaymentNotification(payment, NotificationEvent.NotificationTemplate.PAYMENT_FAILED);

                log.info("Payment failed: {}, reason: {}", 
                    payment.getId(), capturedPayment.getFailureMessage());
            }

            return PaymentCallbackResponse.success(
                payment.getId(),
                payment.getOrderId(),
                payment.getPaymentStatus().name()
            );

        } catch (Exception e) {
            log.error("Error handling callback from provider: {}", providerId, e);
            return PaymentCallbackResponse.error("99", "Internal error: " + e.getMessage());
        }
    }

    // ==================== Refund ====================

    @Override
    @Transactional
    public RefundResponse processRefund(Long paymentId, BigDecimal amount, String reason) {
        log.info("Processing refund for payment: {}, amount: {}, reason: {}", paymentId, amount, reason);

        Payment payment = paymentRepository.findByIdWithLock(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        // Validate payment can be refunded
        if (!payment.canRefund()) {
            throw new PaymentException(paymentId, 
                "Payment cannot be refunded, status: " + payment.getPaymentStatus());
        }

        // Validate refund amount
        if (amount.compareTo(payment.getAmount()) > 0) {
            throw new PaymentException(paymentId, "Refund amount exceeds payment amount");
        }

        // Get gateway that processed original payment
        String providerId = gatewayFactory.getProviderIdForMethod(payment.getPaymentMethod());
        PaymentGateway gateway = gatewayFactory.getGatewayById(providerId);

        // Process refund with gateway
        RefundResponse refundResponse = gateway.processRefund(payment, amount, reason);

        if ("SUCCESS".equals(refundResponse.getStatus())) {
            // Update payment status
            if (amount.compareTo(payment.getAmount()) == 0) {
                payment.markAsRefunded();
            } else {
                payment.setPaymentStatus(PaymentStatus.PARTIALLY_REFUNDED);
            }
            paymentRepository.save(payment);

            // Publish refund event
            publishPaymentRefundedEvent(payment, amount, reason);

            log.info("Refund processed successfully for payment: {}", paymentId);
        }

        return refundResponse;
    }

    // ==================== Query Methods ====================

    @Override
    @Transactional(readOnly = true)
    public PaymentStatus checkPaymentStatus(Long paymentId) {
        log.info("Checking payment status: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        // If in terminal state, return stored status
        if (payment.getPaymentStatus().isTerminal()) {
            return payment.getPaymentStatus();
        }

        // Query gateway for current status
        String providerId = gatewayFactory.getProviderIdForMethod(payment.getPaymentMethod());
        PaymentGateway gateway = gatewayFactory.getGatewayById(providerId);
        
        PaymentStatus gatewayStatus = gateway.queryPaymentStatus(payment.getGatewayTransactionId());
        
        if (gatewayStatus != null && gatewayStatus != payment.getPaymentStatus()) {
            // Update local status
            payment.setPaymentStatus(gatewayStatus);
            paymentRepository.save(payment);
            log.info("Payment status updated from gateway: {} -> {}", payment.getId(), gatewayStatus);
        }

        return payment.getPaymentStatus();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
            .map(paymentMapper::toDTO)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByCheckoutId(String checkoutId) {
        return paymentRepository.findByCheckoutId(checkoutId)
            .map(paymentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PaymentDTO> getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
            .map(paymentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status).stream()
            .map(paymentMapper::toDTO)
            .collect(Collectors.toList());
    }

    // ==================== Cancel / Retry ====================

    @Override
    @Transactional
    public boolean cancelPayment(Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findByIdWithLock(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        if (!payment.isPending()) {
            log.warn("Cannot cancel payment not in pending status: {}", payment.getPaymentStatus());
            return false;
        }

        // Try to cancel with gateway
        String providerId = gatewayFactory.getProviderIdForMethod(payment.getPaymentMethod());
        PaymentGateway gateway = gatewayFactory.getGatewayById(providerId);
        gateway.cancelPayment(payment);

        // Update status
        payment.setPaymentStatus(PaymentStatus.CANCELLED);
        paymentRepository.save(payment);

        // Publish event
        PaymentEvent event = new PaymentEvent(PaymentEvent.PaymentEventType.PAYMENT_FAILED, SOURCE);
        event.setPaymentId(payment.getId());
        event.setOrderId(payment.getOrderId());
        event.setFailureReason("Cancelled by user");
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);

        log.info("Payment cancelled: {}", paymentId);
        return true;
    }

    @Override
    @Transactional
    public PaymentDTO retryFailedPayment(Long paymentId) {
        log.info("Retrying failed payment: {}", paymentId);

        Payment originalPayment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        if (!originalPayment.isFailed() && originalPayment.getPaymentStatus() != PaymentStatus.CANCELLED) {
            throw new PaymentException(paymentId, "Can only retry failed or cancelled payments");
        }

        // Create new payment with same details
        Payment newPayment = Payment.builder()
            .checkoutId(originalPayment.getCheckoutId())
            .orderId(originalPayment.getOrderId())
            .amount(originalPayment.getAmount())
            .paymentFee(originalPayment.getPaymentFee())
            .paymentMethod(originalPayment.getPaymentMethod())
            .paymentStatus(PaymentStatus.PENDING)
            .build();

        Payment savedPayment = paymentRepository.save(newPayment);
        log.info("Created retry payment: {} for original: {}", savedPayment.getId(), paymentId);

        return paymentMapper.toDTO(savedPayment);
    }

    // ==================== Expire Pending Payments ====================

    @Override
    @Transactional
    public int expirePendingPayments() {
        log.info("Expiring pending payments older than {} minutes", paymentExpiryMinutes);

        LocalDateTime expiryCutoff = LocalDateTime.now().minusMinutes(paymentExpiryMinutes);
        List<Payment> pendingPayments = paymentRepository.findPendingPaymentsBefore(expiryCutoff);

        int expiredCount = 0;
        for (Payment payment : pendingPayments) {
            try {
                payment.setPaymentStatus(PaymentStatus.EXPIRED);
                paymentRepository.save(payment);

                // Publish timeout event
                PaymentEvent event = new PaymentEvent(PaymentEvent.PaymentEventType.PAYMENT_TIMEOUT, SOURCE);
                event.setPaymentId(payment.getId());
                event.setOrderId(payment.getOrderId());
                event.setFailureReason("Payment expired after " + paymentExpiryMinutes + " minutes");
                eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);

                expiredCount++;
                log.info("Expired payment: {}", payment.getId());
            } catch (Exception e) {
                log.error("Error expiring payment: {}", payment.getId(), e);
            }
        }

        log.info("Expired {} pending payments", expiredCount);
        return expiredCount;
    }

    // ==================== Update / Confirm ====================

    @Override
    @Transactional
    public PaymentDTO updatePaymentOrderId(Long paymentId, Long orderId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        payment.setOrderId(orderId);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Updated payment {} with orderId: {}", paymentId, orderId);
        return paymentMapper.toDTO(savedPayment);
    }

    @Override
    @Transactional
    public PaymentDTO confirmCODPayment(Long paymentId, String confirmationCode, BigDecimal collectedAmount) {
        log.info("Confirming COD payment: {}, confirmationCode: {}, amount: {}", 
            paymentId, confirmationCode, collectedAmount);

        Payment payment = paymentRepository.findByIdWithLock(paymentId)
            .orElseThrow(() -> new PaymentException(paymentId, "Payment not found: " + paymentId));

        if (payment.getPaymentMethod() != PaymentMethod.COD) {
            throw new PaymentException(paymentId, 
                "Payment is not COD: " + payment.getPaymentMethod());
        }

        if (payment.getPaymentStatus() != PaymentStatus.INITIATED && 
            payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentException(paymentId, 
                "COD payment cannot be confirmed, status: " + payment.getPaymentStatus());
        }

        // Validate collected amount
        if (collectedAmount.compareTo(payment.getTotalAmount()) != 0) {
            log.warn("COD collected amount {} differs from expected {}", 
                collectedAmount, payment.getTotalAmount());
            // Could handle partial payment here
        }

        payment.markAsSuccess(confirmationCode);
        Payment savedPayment = paymentRepository.save(payment);

        // Publish success event
        publishPaymentSuccessEvent(savedPayment);

        log.info("COD payment confirmed: {}", paymentId);
        return paymentMapper.toDTO(savedPayment);
    }

    // ==================== Helper Methods ====================

    private Payment findPaymentFromCallback(String txnRef, String orderId) {
        // Try VNPay transaction reference (payment ID)
        if (txnRef != null) {
            try {
                Long paymentId = Long.parseLong(txnRef);
                return paymentRepository.findById(paymentId).orElse(null);
            } catch (NumberFormatException ignored) {}
        }
        
        // Try order ID (MoMo, PayPal)
        if (orderId != null) {
            try {
                Long paymentId = Long.parseLong(orderId);
                return paymentRepository.findById(paymentId).orElse(null);
            } catch (NumberFormatException ignored) {}
        }

        return null;
    }

    // ==================== Event Publishing ====================

    private void publishPaymentInitiatedEvent(Payment payment) {
        PaymentEvent event = PaymentEvent.paymentInitiated(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getPaymentMethod().name()
        );
        event.setCorrelationId(payment.getCheckoutId());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    private void publishPaymentSuccessEvent(Payment payment) {
        PaymentEvent event = PaymentEvent.paymentSuccess(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            payment.getAmount(),
            payment.getGatewayTransactionId()
        );
        event.setCorrelationId(payment.getCheckoutId());
        event.setPaymentMethod(payment.getPaymentMethod().name());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    private void publishPaymentFailedEvent(Payment payment, String reason) {
        PaymentEvent event = PaymentEvent.paymentFailed(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            reason
        );
        event.setCorrelationId(payment.getCheckoutId());
        event.setPaymentMethod(payment.getPaymentMethod().name());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    private void publishPaymentRefundedEvent(Payment payment, BigDecimal amount, String reason) {
        PaymentEvent event = PaymentEvent.paymentRefunded(
            SOURCE,
            payment.getId(),
            payment.getOrderId(),
            amount,
            reason
        );
        event.setCorrelationId(payment.getCheckoutId());
        eventPublisher.publish(KafkaTopics.PAYMENT_EVENTS, event);
    }

    private void publishPaymentNotification(Payment payment, NotificationEvent.NotificationTemplate template) {
        // This would require customer email from somewhere (order service or request)
        // For now, skip notification if no email available
        log.debug("Notification would be sent for payment: {}, template: {}", payment.getId(), template);
    }
}
