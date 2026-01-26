package com.shopping.microservices.payment_service.service;

import com.shopping.microservices.payment_service.dto.*;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Payment Service interface defining core payment operations.
 */
public interface PaymentService {

    /**
     * Create a new payment record from checkout
     * 
     * @param checkoutId The checkout identifier
     * @param orderId The order identifier (optional)
     * @param amount The payment amount
     * @param paymentMethod The payment method
     * @return Created payment DTO
     */
    PaymentDTO createPayment(String checkoutId, Long orderId, BigDecimal amount, PaymentMethod paymentMethod);

    /**
     * Initiate payment with the payment provider
     * 
     * @param paymentId The payment identifier
     * @param request The initiation request containing return URLs and customer info
     * @return InitiatedPayment with redirect URL or error
     */
    InitiatedPayment initiatePayment(Long paymentId, InitiatePaymentRequest request);

    /**
     * Handle callback from payment provider
     * 
     * @param providerId The provider identifier (VNPAY, MOMO, etc.)
     * @param params The callback parameters
     * @return Callback response for the provider
     */
    PaymentCallbackResponse handleCallback(String providerId, Map<String, String> params);

    /**
     * Process refund for a payment
     * 
     * @param paymentId The payment identifier
     * @param amount The refund amount
     * @param reason The refund reason
     * @return Refund response
     */
    RefundResponse processRefund(Long paymentId, BigDecimal amount, String reason);

    /**
     * Query current payment status
     * 
     * @param paymentId The payment identifier
     * @return Current payment status
     */
    PaymentStatus checkPaymentStatus(Long paymentId);

    /**
     * Get payment by ID
     * 
     * @param paymentId The payment identifier
     * @return Payment DTO
     */
    PaymentDTO getPaymentById(Long paymentId);

    /**
     * Get payment by checkout ID
     * 
     * @param checkoutId The checkout identifier
     * @return Optional payment DTO
     */
    Optional<PaymentDTO> getPaymentByCheckoutId(String checkoutId);

    /**
     * Get payment by order ID
     * 
     * @param orderId The order identifier
     * @return Optional payment DTO
     */
    Optional<PaymentDTO> getPaymentByOrderId(Long orderId);

    /**
     * Get payments by status
     * 
     * @param status The payment status
     * @return List of payments
     */
    List<PaymentDTO> getPaymentsByStatus(PaymentStatus status);

    /**
     * Cancel a pending payment
     * 
     * @param paymentId The payment identifier
     * @return true if cancelled successfully
     */
    boolean cancelPayment(Long paymentId);

    /**
     * Retry a failed payment
     * 
     * @param paymentId The original payment identifier
     * @return New payment DTO
     */
    PaymentDTO retryFailedPayment(Long paymentId);

    /**
     * Expire pending payments older than configured timeout
     * 
     * @return Number of payments expired
     */
    int expirePendingPayments();

    /**
     * Update payment with order ID after order creation
     * 
     * @param paymentId The payment identifier
     * @param orderId The order identifier
     * @return Updated payment DTO
     */
    PaymentDTO updatePaymentOrderId(Long paymentId, Long orderId);

    /**
     * Confirm COD payment (called by delivery service or admin)
     * 
     * @param paymentId The payment identifier
     * @param confirmationCode The delivery confirmation code
     * @param collectedAmount The amount collected
     * @return Updated payment DTO
     */
    PaymentDTO confirmCODPayment(Long paymentId, String confirmationCode, BigDecimal collectedAmount);
}
