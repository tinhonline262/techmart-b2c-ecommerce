package com.shopping.microservices.payment_service.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.common_library.constants.KafkaConsumerGroups;
import com.shopping.microservices.common_library.constants.KafkaTopics;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Kafka listener for order events.
 * Handles ORDER_CREATED events to create payment records
 * and ORDER_CANCELLED events to cancel pending payments.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = KafkaTopics.ORDER_EVENTS,
        groupId = KafkaConsumerGroups.PAYMENT_SERVICE,
        containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void handleOrderEvent(String message, Acknowledgment ack) {
        try {
            OrderEvent event = objectMapper.readValue(message, OrderEvent.class);
            log.info("Received OrderEvent: type={}, orderId={}, orderNumber={}",
                event.getEventType(), event.getOrderId(), event.getOrderNumber());

            switch (event.getEventType()) {
                case "ORDER_CREATED" -> {
                    log.info("Processing ORDER_CREATED for order: {}", event.getOrderNumber());
                    handleOrderCreated(event);
                }
                case "ORDER_CANCELLED" -> {
                    log.info("Processing ORDER_CANCELLED for order: {}", event.getOrderNumber());
                    handleOrderCancelled(event);
                }
                default -> log.debug("Ignoring event type: {}", event.getEventType());
            }

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing OrderEvent: {}", e.getMessage(), e);
            // Don't acknowledge - message will be retried
        }
    }

    private void handleOrderCreated(OrderEvent event) {
        try {
            // Extract payment details from order event
            BigDecimal totalAmount = event.getTotalAmount();
            Long orderId = event.getOrderId();
            String checkoutId = extractCheckoutId(event);


            // Determine payment method from event metadata
            PaymentMethod paymentMethod = extractPaymentMethod(event);
            
            if (checkoutId == null) {
                checkoutId = "ORDER_" + orderId;
            }

            // Check if payment already exists
            var existingPayment = paymentService.getPaymentByOrderId(orderId);
            if (existingPayment.isPresent()) {
                log.info("Payment already exists for order: {}, paymentId: {}", 
                    orderId, existingPayment.get().getId());
                return;
            }

            // Create payment record
            var paymentDTO = paymentService.createPayment(checkoutId, orderId, totalAmount, paymentMethod);
            log.info("Created payment {} for order: {}", paymentDTO.getId(), orderId);

            // For online payment methods, we need the order service to call initiatePayment
            // with return URLs. For COD, payment is ready.
            if (paymentMethod == PaymentMethod.COD) {
                paymentService.initiatePayment(paymentDTO.getId());
                log.info("Auto-initiated COD payment for order: {}", orderId);
            }

        } catch (Exception e) {
            log.error("Error handling ORDER_CREATED for order: {}", event.getOrderId(), e);
            throw e;
        }
    }

    private void handleOrderCancelled(OrderEvent event) {
        try {
            var paymentOpt = paymentService.getPaymentByOrderId(event.getOrderId());
            
            if (paymentOpt.isPresent()) {
                var payment = paymentOpt.get();
                if (payment.getPaymentStatus().isTerminal()) {
                    log.info("Payment already in terminal state for cancelled order: {}", event.getOrderId());
                    return;
                }
                
                boolean cancelled = paymentService.cancelPayment(payment.getId());
                if (cancelled) {
                    log.info("Cancelled payment {} for cancelled order: {}", 
                        payment.getId(), event.getOrderId());
                }
            } else {
                log.info("No payment found for cancelled order: {}", event.getOrderId());
            }
        } catch (Exception e) {
            log.error("Error handling ORDER_CANCELLED for order: {}", event.getOrderId(), e);
            throw e;
        }
    }

    private PaymentMethod extractPaymentMethod(OrderEvent event) {
        if (event.getMetadata() != null) {
            Object methodObj = event.getMetadata().get("paymentMethod");
            if (methodObj != null) {
                try {
                    return PaymentMethod.valueOf(methodObj.toString().toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown payment method in order event: {}", methodObj);
                }
            }
        }
        // Default to COD if not specified
        return PaymentMethod.COD;
    }

    private String extractCheckoutId(OrderEvent event) {
        if (event.getMetadata() != null) {
            Object checkoutIdObj = event.getMetadata().get("checkoutId");
            if (checkoutIdObj != null) {
                // Use correlationId if available, else use checkoutId from metadata
                return event.getCorrelationId() != null ? event.getCorrelationId() : checkoutIdObj.toString();
            }
        }
        return null;
    }
}
