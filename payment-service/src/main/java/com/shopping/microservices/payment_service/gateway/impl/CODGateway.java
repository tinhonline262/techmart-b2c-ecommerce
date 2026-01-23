package com.shopping.microservices.payment_service.gateway.impl;

import com.shopping.microservices.payment_service.dto.CapturedPayment;
import com.shopping.microservices.payment_service.dto.InitiatePaymentRequest;
import com.shopping.microservices.payment_service.dto.InitiatedPayment;
import com.shopping.microservices.payment_service.dto.RefundResponse;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import com.shopping.microservices.payment_service.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Cash on Delivery (COD) Payment Gateway implementation.
 * 
 * This is a stateless implementation handling offline payment method.
 * No external API calls needed - only business logic for:
 * - Payment initiation (marking as pending for delivery)
 * - Payment confirmation (triggered by delivery personnel/admin)
 * - Business rules validation (amount limits, location restrictions)
 */
@Service("COD")
@Slf4j
public class CODGateway implements PaymentGateway {

    @Value("${payment.cod.max-amount:10000000}")
    private BigDecimal maxAmount; // Default 10 million VND

    @Value("${payment.cod.min-amount:10000}")
    private BigDecimal minAmount; // Default 10,000 VND

    @Value("${payment.cod.service-fee:0}")
    private BigDecimal serviceFee; // COD service fee

    @Value("${payment.cod.enabled:true}")
    private boolean enabled;

    // List of restricted provinces (for demo purposes)
    private static final Set<String> RESTRICTED_PROVINCES = Set.of(
        "HA_GIANG", "CAO_BANG", "LAO_CAI", "BAC_KAN", "LANG_SON", "DIEN_BIEN"
    );

    // List of restricted product categories
    private static final Set<String> RESTRICTED_CATEGORIES = Set.of(
        "ELECTRONICS_EXPENSIVE", "JEWELRY", "LUXURY_GOODS"
    );

    @Override
    public String getProviderId() {
        return "COD";
    }

    @Override
    public List<PaymentMethod> getSupportedMethods() {
        return Collections.singletonList(PaymentMethod.COD);
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return method == PaymentMethod.COD;
    }

    @Override
    public InitiatedPayment initiatePayment(Payment payment, InitiatePaymentRequest request) {
        log.info("Initiating COD payment for payment ID: {}, amount: {}", 
            payment.getId(), payment.getTotalAmount());

        try {
            // Validate COD eligibility
            ValidationResult validation = validateCODEligibility(payment, request);
            if (!validation.isValid()) {
                log.warn("COD payment not eligible for payment ID: {}, reason: {}", 
                    payment.getId(), validation.getMessage());
                return InitiatedPayment.builder()
                    .status("failed")
                    .paymentId(payment.getId().toString())
                    .additionalData(Map.of(
                        "error", validation.getMessage(),
                        "errorCode", validation.getErrorCode()
                    ))
                    .build();
            }

            // Generate COD reference code
            String codReferenceCode = generateCODReferenceCode(payment);

            // Build metadata for COD payment
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("codReferenceCode", codReferenceCode);
            additionalData.put("codServiceFee", serviceFee);
            additionalData.put("totalWithFee", payment.getTotalAmount().add(serviceFee));
            additionalData.put("paymentInstructions", buildPaymentInstructions(payment));

            if (request.getCustomerInfo() != null) {
                additionalData.put("deliveryPhone", request.getCustomerInfo().getPhone());
                additionalData.put("deliveryName", request.getCustomerInfo().getFullName());
            }

            log.info("COD payment initiated successfully for payment ID: {}, reference: {}", 
                payment.getId(), codReferenceCode);

            // COD doesn't require redirect - payment is collected on delivery
            return InitiatedPayment.builder()
                .status("success")
                .paymentId(payment.getId().toString())
                .redirectUrl(null) // No redirect needed
                .qrCode(null)
                .deepLink(null)
                .expiresAt(null) // No expiration - handled at order level
                .additionalData(additionalData)
                .build();

        } catch (Exception e) {
            log.error("Error initiating COD payment for payment ID: {}", payment.getId(), e);
            return InitiatedPayment.builder()
                .status("failed")
                .paymentId(payment.getId().toString())
                .additionalData(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        // COD callbacks are typically internal (from delivery service or admin)
        // Verify the delivery confirmation code
        String confirmationCode = params.get("confirmationCode");
        String deliveryPersonId = params.get("deliveryPersonId");
        String orderId = params.get("orderId");

        if (confirmationCode == null || orderId == null) {
            log.warn("COD callback missing required parameters");
            return false;
        }

        // In a real implementation, you would verify:
        // 1. The delivery person is authorized
        // 2. The confirmation code is valid
        // 3. The order is in correct state for COD collection

        log.info("COD callback verified for orderId: {}, deliveryPerson: {}", orderId, deliveryPersonId);
        return true;
    }

    @Override
    public CapturedPayment processCallback(Map<String, String> params) {
        log.info("Processing COD confirmation callback");

        String orderId = params.get("orderId");
        String confirmationCode = params.get("confirmationCode");
        String collectedAmount = params.get("collectedAmount");
        String status = params.getOrDefault("status", "SUCCESS");

        PaymentStatus paymentStatus = switch (status.toUpperCase()) {
            case "SUCCESS", "COLLECTED" -> PaymentStatus.SUCCESS;
            case "PARTIAL" -> PaymentStatus.PARTIALLY_REFUNDED; // Partial collection
            case "REJECTED", "REFUSED" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };

        String failureMessage = null;
        if (paymentStatus == PaymentStatus.FAILED) {
            failureMessage = params.getOrDefault("reason", "Khách hàng từ chối nhận hàng");
        }

        return CapturedPayment.builder()
            .orderId(orderId != null ? Long.parseLong(orderId) : null)
            .amount(collectedAmount != null ? new BigDecimal(collectedAmount) : BigDecimal.ZERO)
            .paymentFee(serviceFee)
            .gatewayTransactionId(confirmationCode)
            .paymentMethod(PaymentMethod.COD)
            .paymentStatus(paymentStatus)
            .failureMessage(failureMessage)
            .metadata(Map.of(
                "deliveryPersonId", params.getOrDefault("deliveryPersonId", ""),
                "collectedAt", params.getOrDefault("collectedAt", LocalDateTime.now().toString()),
                "deliveryNotes", params.getOrDefault("notes", "")
            ))
            .build();
    }

    @Override
    public PaymentStatus queryPaymentStatus(String transactionId) {
        // COD payment status is managed internally
        // In a real implementation, this would query the delivery service
        log.info("Querying COD payment status for reference: {}", transactionId);
        
        // Return null to indicate status should be fetched from local database
        return null;
    }

    @Override
    public RefundResponse processRefund(Payment payment, BigDecimal amount, String reason) {
        log.info("Processing COD refund for payment: {}, amount: {}", payment.getId(), amount);

        // COD refunds are typically handled differently:
        // 1. If goods not yet delivered - cancel order
        // 2. If goods delivered and paid - refund through other means (bank transfer)

        if (!payment.isSuccessful()) {
            log.warn("Cannot refund COD payment that is not successful: {}", payment.getId());
            return RefundResponse.builder()
                .status("FAILED")
                .refundedAmount(BigDecimal.ZERO)
                .build();
        }

        // Generate refund reference
        String refundId = "COD_REFUND_" + payment.getId() + "_" + System.currentTimeMillis();

        // In real implementation:
        // 1. Create refund request for customer service
        // 2. Initiate bank transfer to customer
        // 3. Track refund completion

        return RefundResponse.builder()
            .refundId(refundId)
            .status("PENDING") // COD refunds require manual processing
            .refundedAmount(amount)
            .refundedAt(LocalDateTime.now())
            .estimatedCompletionDate(LocalDateTime.now().plusDays(7).toLocalDate())
            .build();
    }

    @Override
    public boolean cancelPayment(Payment payment) {
        // COD payments can be cancelled if order not yet shipped
        log.info("Cancelling COD payment: {}", payment.getId());

        if (payment.isPending()) {
            // Simply mark as cancelled - no external API call needed
            return true;
        }

        log.warn("Cannot cancel COD payment that is not pending: {}", payment.getId());
        return false;
    }

    // ==================== Business Rules Validation ====================

    /**
     * Validate if COD is available for this payment based on business rules
     */
    public ValidationResult validateCODEligibility(Payment payment, InitiatePaymentRequest request) {
        // Check if COD is enabled
        if (!enabled) {
            return ValidationResult.invalid("COD_DISABLED", "Thanh toán khi nhận hàng tạm thời không khả dụng");
        }

        BigDecimal amount = payment.getTotalAmount();

        // Check amount limits
        if (amount.compareTo(maxAmount) > 0) {
            return ValidationResult.invalid("AMOUNT_TOO_HIGH", 
                String.format("Số tiền vượt quá hạn mức COD (%s VND)", maxAmount.toPlainString()));
        }

        if (amount.compareTo(minAmount) < 0) {
            return ValidationResult.invalid("AMOUNT_TOO_LOW", 
                String.format("Số tiền tối thiểu cho COD là %s VND", minAmount.toPlainString()));
        }

        // Check for restricted delivery areas (from metadata or request)
        if (request.getMetadata() != null) {
            String province = (String) request.getMetadata().get("deliveryProvince");
            if (province != null && RESTRICTED_PROVINCES.contains(province.toUpperCase())) {
                return ValidationResult.invalid("RESTRICTED_AREA", 
                    "COD không khả dụng cho khu vực giao hàng này");
            }

            // Check for restricted product categories
            @SuppressWarnings("unchecked")
            List<String> categories = (List<String>) request.getMetadata().get("productCategories");
            if (categories != null) {
                for (String category : categories) {
                    if (RESTRICTED_CATEGORIES.contains(category.toUpperCase())) {
                        return ValidationResult.invalid("RESTRICTED_PRODUCT", 
                            "Một số sản phẩm trong đơn hàng không hỗ trợ COD");
                    }
                }
            }

            // Check customer trust score (for fraud prevention)
            Object trustScoreObj = request.getMetadata().get("customerTrustScore");
            if (trustScoreObj instanceof Number) {
                int trustScore = ((Number) trustScoreObj).intValue();
                if (trustScore < 30) { // Low trust score threshold
                    return ValidationResult.invalid("LOW_TRUST_SCORE", 
                        "COD không khả dụng cho tài khoản này. Vui lòng chọn phương thức thanh toán khác");
                }
            }
        }

        return ValidationResult.valid();
    }

    /**
     * Calculate COD service fee based on order details
     */
    public BigDecimal calculateServiceFee(Payment payment, InitiatePaymentRequest request) {
        // Base service fee
        BigDecimal fee = serviceFee;

        // Additional fee based on amount tiers
        BigDecimal amount = payment.getTotalAmount();
        if (amount.compareTo(new BigDecimal("5000000")) > 0) {
            fee = fee.add(new BigDecimal("20000")); // Extra 20k for orders > 5M
        }

        // Remote area surcharge
        if (request.getMetadata() != null) {
            Boolean isRemoteArea = (Boolean) request.getMetadata().get("isRemoteArea");
            if (Boolean.TRUE.equals(isRemoteArea)) {
                fee = fee.add(new BigDecimal("30000")); // Remote area surcharge
            }
        }

        return fee;
    }

    // ==================== Helper Methods ====================

    private String generateCODReferenceCode(Payment payment) {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        return "COD" + payment.getId() + timestamp;
    }

    private String buildPaymentInstructions(Payment payment) {
        return String.format(
            "Vui lòng thanh toán %s VND cho nhân viên giao hàng khi nhận hàng. " +
            "Kiểm tra hàng hóa trước khi thanh toán.",
            payment.getTotalAmount().add(serviceFee).toPlainString()
        );
    }

    // ==================== Inner Classes ====================

    /**
     * Validation result for COD eligibility check
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorCode;
        private final String message;

        private ValidationResult(boolean valid, String errorCode, String message) {
            this.valid = valid;
            this.errorCode = errorCode;
            this.message = message;
        }

        public static ValidationResult valid() {
            return new ValidationResult(true, null, null);
        }

        public static ValidationResult invalid(String errorCode, String message) {
            return new ValidationResult(false, errorCode, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public String getMessage() {
            return message;
        }
    }
}
