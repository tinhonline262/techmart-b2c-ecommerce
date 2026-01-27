package com.shopping.microservices.payment_service.controller;

import com.shopping.microservices.common_library.dto.ApiResponse;
import com.shopping.microservices.payment_service.dto.*;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import com.shopping.microservices.payment_service.gateway.PaymentGatewayFactory;
import com.shopping.microservices.payment_service.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST Controller for payment operations.
 * 
 * Provides endpoints for:
 * - Payment initiation
 * - Payment callbacks from providers
 * - Payment status queries
 * - Refund processing
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentGatewayFactory gatewayFactory;

    /**
     * Create a new payment
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(
            @RequestParam String checkoutId,
            @RequestParam(required = false) Long orderId,
            @RequestParam BigDecimal amount,
            @RequestParam PaymentMethod paymentMethod) {
        
        log.info("Creating payment: checkoutId={}, orderId={}, amount={}, method={}",
            checkoutId, orderId, amount, paymentMethod);

        PaymentDTO payment = paymentService.createPayment(checkoutId, orderId, amount, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    /**
     * Initiate payment with provider
     */
    @GetMapping("/{orderId}/initiate")
    public ResponseEntity<ApiResponse<InitiatedPayment>> initiatePayment(
            @PathVariable Long orderId) {
        
        log.info("Initiating payment with orderId: {}", orderId);

        InitiatedPayment result = paymentService.initiatePayment(orderId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Handle callback from VNPay
     */
    @GetMapping("/callback/vnpay")
    public ResponseEntity<String> handleVNPayCallback(HttpServletRequest request) {
        log.info("Received VNPay callback");
        
        Map<String, String> params = extractCallbackParams(request);
        PaymentCallbackResponse response = paymentService.handleCallback("VNPAY", params);
        
        // VNPay expects specific response format
        String vnpResponse = String.format("{\"RspCode\":\"%s\",\"Message\":\"%s\"}", 
            response.getResponseCode(), response.getMessage());
        
        return ResponseEntity.ok(vnpResponse);
    }

    /**
     * Handle COD payment confirmation
     */
    @PostMapping("/callback/cod")
    public ResponseEntity<ApiResponse<PaymentDTO>> confirmCODPayment(
            @RequestParam Long paymentId,
            @RequestParam String confirmationCode,
            @RequestParam BigDecimal collectedAmount) {
        
        log.info("Confirming COD payment: {}, confirmationCode: {}", paymentId, confirmationCode);
        
        PaymentDTO payment = paymentService.confirmCODPayment(paymentId, confirmationCode, collectedAmount);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPayment(@PathVariable Long paymentId) {
        PaymentDTO payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success(payment));
    }

    /**
     * Get payment by checkout ID
     */
    @GetMapping("/checkout/{checkoutId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByCheckout(@PathVariable String checkoutId) {
        return paymentService.getPaymentByCheckoutId(checkoutId)
            .map(p -> ResponseEntity.ok(ApiResponse.success(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId)
            .map(p -> ResponseEntity.ok(ApiResponse.success(p)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check payment status
     */
    @GetMapping("/{paymentId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPaymentStatus(@PathVariable Long paymentId) {
        PaymentStatus status = paymentService.checkPaymentStatus(paymentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", paymentId);
        response.put("status", status.name());
        response.put("displayName", status.getDisplayName());
        response.put("isTerminal", status.isTerminal());
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get payments by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }

    /**
     * Process refund
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<ApiResponse<RefundResponse>> processRefund(
            @PathVariable Long paymentId,
            @Valid @RequestBody RefundPaymentRequest request) {
        
        log.info("Processing refund for payment: {}, amount: {}", paymentId, request.getRefundAmount());
        
        RefundResponse response = paymentService.processRefund(
            paymentId, 
            request.getRefundAmount(), 
            request.getReason()
        );
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Cancel payment
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelPayment(@PathVariable Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);
        
        boolean cancelled = paymentService.cancelPayment(paymentId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("paymentId", paymentId);
        response.put("cancelled", cancelled);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Retry failed payment
     */
    @PostMapping("/{paymentId}/retry")
    public ResponseEntity<ApiResponse<PaymentDTO>> retryPayment(@PathVariable Long paymentId) {
        log.info("Retrying payment: {}", paymentId);
        
        PaymentDTO newPayment = paymentService.retryFailedPayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success(newPayment));
    }

    /**
     * Get supported payment methods
     */
    @GetMapping("/methods")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSupportedMethods() {
        Set<PaymentMethod> methods = gatewayFactory.getSupportedMethods();
        
        List<Map<String, Object>> methodList = methods.stream()
            .map(m -> {
                Map<String, Object> methodInfo = new HashMap<>();
                methodInfo.put("code", m.name());
                methodInfo.put("displayName", m.getDisplayName());
                methodInfo.put("provider", gatewayFactory.getProviderIdForMethod(m));
                return methodInfo;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(methodList));
    }

    /**
     * Check if payment method is available
     */
    @GetMapping("/methods/{method}/available")
    public ResponseEntity<ApiResponse<Map<String, Object>>> isMethodAvailable(@PathVariable PaymentMethod method) {
        boolean available = gatewayFactory.isMethodSupported(method);
        
        Map<String, Object> response = new HashMap<>();
        response.put("method", method.name());
        response.put("available", available);
        if (available) {
            response.put("provider", gatewayFactory.getProviderIdForMethod(method));
        }
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ==================== Helper Methods ====================

    private Map<String, String> extractCallbackParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            params.put(paramName, request.getParameter(paramName));
        }
        return params;
    }
}
