package com.shopping.microservices.payment_service.service.impl;

import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import com.shopping.microservices.payment_service.event.PaymentCompletedEvent;
import com.shopping.microservices.payment_service.event.PaymentFailedEvent;
import com.shopping.microservices.payment_service.kafka.producer.PaymentEventProducer;
import com.shopping.microservices.payment_service.repository.PaymentRepository;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventProducer paymentEventProducer;

    @Override
    public Payment createPayment(Long orderId, String orderNumber, BigDecimal amount, PaymentMethod paymentMethod) {
        log.info("Creating payment for order: {}, amount: {}, method: {}", orderNumber, amount, paymentMethod);

        Payment payment = Payment.builder()
                .orderId(orderId)
                .orderNumber(orderNumber)
                .amount(amount)
                .currency("VND")
                .paymentMethod(paymentMethod)
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with id: {}", savedPayment.getId());

        return savedPayment;
    }

    @Override
    public Payment processPayment(Long paymentId) {
        log.info("Processing payment with id: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with id: " + paymentId));

        if (payment.getPaymentMethod() == PaymentMethod.CASH) {
            // CASH payment: Process immediately
            processCashPayment(payment);
        } else if (payment.getPaymentMethod() == PaymentMethod.BANK_TRANSFER) {
            // BANK_TRANSFER: Mock 15 second timeout then fail
            processBankTransferPayment(payment);
        }

        return payment;
    }

    private void processCashPayment(Payment payment) {
        log.info("Processing CASH payment for order: {}", payment.getOrderNumber());

        try {
            // Update payment status
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setProviderTransactionId("CASH-" + UUID.randomUUID());
            payment.setPaymentProvider("CASH");
            paymentRepository.save(payment);

            // Publish success event immediately
            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .orderId(payment.getOrderId())
                    .orderNumber(payment.getOrderNumber())
                    .paymentId(payment.getId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .message("Cash payment completed successfully")
                    .build();

            paymentEventProducer.publishPaymentCompletedEvent(event);
            log.info("CASH payment completed for order: {}", payment.getOrderNumber());

        } catch (Exception e) {
            log.error("Error processing CASH payment", e);
            handlePaymentFailure(payment, "Error processing cash payment: " + e.getMessage());
        }
    }

    @Async
    public void processBankTransferPayment(Payment payment) {
        log.info("Processing BANK_TRANSFER payment for order: {}", payment.getOrderNumber());

        try {
            // Update to processing
            payment.setStatus(PaymentStatus.PROCESSING);
            payment.setPaymentProvider("BANK");
            payment.setProviderTransactionId("BANK-" + UUID.randomUUID());
            paymentRepository.save(payment);

            log.info("Waiting 15 seconds for bank transfer payment...");

            Thread.sleep(5000);

            log.warn("Bank transfer payment timeout for order: {}", payment.getOrderNumber());

            // Update payment status to failed
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);

            // Publish failure event
            PaymentFailedEvent event = PaymentFailedEvent.builder()
                    .orderId(payment.getOrderId())
                    .orderNumber(payment.getOrderNumber())
                    .paymentId(payment.getId())
                    .amount(payment.getAmount())
                    .paymentMethod(payment.getPaymentMethod().name())
                    .reason("Bank transfer payment timeout after 15 seconds")
                    .build();

            paymentEventProducer.publishPaymentFailedEvent(event);
            log.info("BANK_TRANSFER payment failed for order: {}", payment.getOrderNumber());

        } catch (InterruptedException e) {
            log.error("Bank transfer payment interrupted", e);
            Thread.currentThread().interrupt();
            handlePaymentFailure(payment, "Bank transfer interrupted: " + e.getMessage());
        } catch (Exception e) {
            log.error("Error processing BANK_TRANSFER payment", e);
            handlePaymentFailure(payment, "Error processing bank transfer: " + e.getMessage());
        }
    }

    private void handlePaymentFailure(Payment payment, String reason) {
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        PaymentFailedEvent event = PaymentFailedEvent.builder()
                .orderId(payment.getOrderId())
                .orderNumber(payment.getOrderNumber())
                .paymentId(payment.getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .reason(reason)
                .build();

        paymentEventProducer.publishPaymentFailedEvent(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for orderId: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Payment getPaymentByOrderNumber(String orderNumber) {
        return paymentRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for orderNumber: " + orderNumber));
    }
}

