package com.shopping.microservices.payment_service.gateway.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MoMo Payment Gateway implementation.
 * 
 * Supports MoMo Partner API v2.0 with payment methods:
 * - MOMO Wallet (captureWallet)
 * - ATM via MoMo (payWithATM)
 * - Credit Card via MoMo (payWithCC)
 * 
 * Authentication: HMAC SHA256 signature
 */
@Service("MOMO")
@Slf4j
public class MoMoGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payment.momo.partner-code}")
    private String partnerCode;

    @Value("${payment.momo.access-key}")
    private String accessKey;

    @Value("${payment.momo.secret-key}")
    private String secretKey;

    @Value("${payment.momo.api-url}")
    private String apiUrl;

    @Value("${payment.momo.return-url}")
    private String returnUrl;

    @Value("${payment.momo.ipn-url}")
    private String ipnUrl;

    // MoMo API endpoints
    private static final String CREATE_PAYMENT_ENDPOINT = "/v2/gateway/api/create";
    private static final String QUERY_PAYMENT_ENDPOINT = "/v2/gateway/api/query";
    private static final String REFUND_ENDPOINT = "/v2/gateway/api/refund";

    // MoMo request types
    private static final String REQUEST_TYPE_CAPTURE_WALLET = "captureWallet";
    private static final String REQUEST_TYPE_PAY_WITH_ATM = "payWithATM";
    private static final String REQUEST_TYPE_PAY_WITH_CC = "payWithCC";

    // Payment link expiration (5 minutes)
    private static final int PAYMENT_EXPIRY_MINUTES = 5;

    public MoMoGateway(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderId() {
        return "MOMO";
    }

    @Override
    public List<PaymentMethod> getSupportedMethods() {
        return Arrays.asList(
            PaymentMethod.MOMO,
            PaymentMethod.ATM_CARD,
            PaymentMethod.CREDIT_CARD,
            PaymentMethod.WALLET
        );
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return getSupportedMethods().contains(method);
    }

    @Override
    public InitiatedPayment initiatePayment(Payment payment, InitiatePaymentRequest request) {
        log.info("Initiating MoMo payment for payment ID: {}, amount: {}", 
            payment.getId(), payment.getTotalAmount());

        try {
            // Generate unique request ID: timestamp + UUID
            String requestId = generateRequestId();
            String orderId = payment.getId().toString();
            long amount = payment.getTotalAmount().longValue();
            String orderInfo = buildOrderInfo(payment, request);
            String extraData = encodeExtraData(request.getMetadata());
            String requestType = getRequestType(payment.getPaymentMethod());
            String effectiveReturnUrl = request.getReturnUrl() != null ? request.getReturnUrl() : returnUrl;
            String effectiveIpnUrl = request.getCallbackUrl() != null ? request.getCallbackUrl() : ipnUrl;

            // Build raw signature string
            String rawSignature = buildRawSignature(
                accessKey, amount, extraData, effectiveIpnUrl, orderId, 
                orderInfo, partnerCode, effectiveReturnUrl, requestId, requestType
            );

            // Calculate HMAC SHA256 signature
            String signature = hmacSHA256(secretKey, rawSignature);

            // Build request body
            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("partnerName", "Online Shopping");
            requestBody.put("storeId", partnerCode);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", amount);
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", orderInfo);
            requestBody.put("redirectUrl", effectiveReturnUrl);
            requestBody.put("ipnUrl", effectiveIpnUrl);
            requestBody.put("lang", request.getLocale() != null ? request.getLocale() : "vi");
            requestBody.put("extraData", extraData);
            requestBody.put("requestType", requestType);
            requestBody.put("signature", signature);

            // Add customer info if available
            if (request.getCustomerInfo() != null) {
                Map<String, String> userInfo = new HashMap<>();
                if (request.getCustomerInfo().getFullName() != null) {
                    userInfo.put("name", request.getCustomerInfo().getFullName());
                }
                if (request.getCustomerInfo().getPhone() != null) {
                    userInfo.put("phoneNumber", request.getCustomerInfo().getPhone());
                }
                if (request.getCustomerInfo().getEmail() != null) {
                    userInfo.put("email", request.getCustomerInfo().getEmail());
                }
                if (!userInfo.isEmpty()) {
                    requestBody.put("userInfo", userInfo);
                }
            }

            // Send request to MoMo API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.debug("Sending MoMo payment request: orderId={}, requestId={}", orderId, requestId);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + CREATE_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );

            return parseCreatePaymentResponse(response.getBody(), payment.getId().toString(), requestId);

        } catch (Exception e) {
            log.error("Error initiating MoMo payment for payment ID: {}", payment.getId(), e);
            return InitiatedPayment.builder()
                .status("failed")
                .paymentId(payment.getId().toString())
                .additionalData(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            String receivedSignature = params.get("signature");
            if (receivedSignature == null) {
                log.warn("MoMo callback missing signature");
                return false;
            }

            // Build raw signature from callback params for verification
            String accessKey = params.get("accessKey");
            String amount = params.get("amount");
            String extraData = params.getOrDefault("extraData", "");
            String message = params.getOrDefault("message", "");
            String orderId = params.get("orderId");
            String orderInfo = params.getOrDefault("orderInfo", "");
            String orderType = params.getOrDefault("orderType", "");
            String partnerCode = params.get("partnerCode");
            String payType = params.getOrDefault("payType", "");
            String requestId = params.get("requestId");
            String responseTime = params.get("responseTime");
            String resultCode = params.get("resultCode");
            String transId = params.get("transId");

            String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&orderType=%s&partnerCode=%s&payType=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                accessKey, amount, extraData, message, orderId, orderInfo, orderType, 
                partnerCode, payType, requestId, responseTime, resultCode, transId
            );

            String calculatedSignature = hmacSHA256(this.secretKey, rawSignature);
            boolean isValid = receivedSignature.equals(calculatedSignature);

            if (!isValid) {
                log.warn("MoMo callback signature verification failed for orderId: {}", orderId);
            }

            return isValid;

        } catch (Exception e) {
            log.error("Error verifying MoMo callback signature", e);
            return false;
        }
    }

    @Override
    public CapturedPayment processCallback(Map<String, String> params) {
        log.info("Processing MoMo callback");

        String orderId = params.get("orderId");
        String resultCode = params.get("resultCode");
        String transId = params.get("transId");
        String amount = params.get("amount");
        String message = params.get("message");
        String payType = params.get("payType");

        // Map MoMo result code to payment status
        PaymentStatus status = mapResultCodeToStatus(resultCode);
        String failureMessage = null;

        if (!"0".equals(resultCode)) {
            failureMessage = getMoMoErrorMessage(resultCode, message);
        }

        PaymentMethod paymentMethod = mapPayTypeToMethod(payType);

        return CapturedPayment.builder()
            .orderId(null) // Will be set from payment record
            .checkoutId(null)
            .amount(amount != null ? new BigDecimal(amount) : BigDecimal.ZERO)
            .gatewayTransactionId(transId)
            .paymentMethod(paymentMethod)
            .paymentStatus(status)
            .failureMessage(failureMessage)
            .metadata(Map.of(
                "momoResultCode", resultCode,
                "momoMessage", message != null ? message : "",
                "payType", payType != null ? payType : ""
            ))
            .build();
    }

    @Override
    public PaymentStatus queryPaymentStatus(String transactionId) {
        log.info("Querying MoMo payment status for transaction: {}", transactionId);

        try {
            String requestId = generateRequestId();

            String rawSignature = String.format(
                "accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                accessKey, transactionId, partnerCode, requestId
            );
            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("requestId", requestId);
            requestBody.put("orderId", transactionId);
            requestBody.put("signature", signature);
            requestBody.put("lang", "vi");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + QUERY_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode responseNode = objectMapper.readTree(response.getBody());
            String resultCode = responseNode.path("resultCode").asText();

            return mapResultCodeToStatus(resultCode);

        } catch (Exception e) {
            log.error("Error querying MoMo payment status", e);
            return null;
        }
    }

    @Override
    public RefundResponse processRefund(Payment payment, BigDecimal amount, String reason) {
        log.info("Processing MoMo refund for payment: {}, amount: {}", payment.getId(), amount);

        try {
            String requestId = generateRequestId();
            String orderId = "REFUND_" + payment.getId() + "_" + System.currentTimeMillis();
            String transId = payment.getGatewayTransactionId();
            long refundAmount = amount.longValue();

            String rawSignature = String.format(
                "accessKey=%s&amount=%d&description=%s&orderId=%s&partnerCode=%s&requestId=%s&transId=%s",
                accessKey, refundAmount, reason, orderId, partnerCode, requestId, transId
            );
            String signature = hmacSHA256(secretKey, rawSignature);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("partnerCode", partnerCode);
            requestBody.put("orderId", orderId);
            requestBody.put("requestId", requestId);
            requestBody.put("amount", refundAmount);
            requestBody.put("transId", transId);
            requestBody.put("lang", "vi");
            requestBody.put("description", reason);
            requestBody.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + REFUND_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode responseNode = objectMapper.readTree(response.getBody());
            String resultCode = responseNode.path("resultCode").asText();
            String transIdResponse = responseNode.path("transId").asText();

            if ("0".equals(resultCode)) {
                return RefundResponse.builder()
                    .refundId(transIdResponse)
                    .status("SUCCESS")
                    .refundedAmount(amount)
                    .refundedAt(LocalDateTime.now())
                    .build();
            } else {
                return RefundResponse.builder()
                    .refundId(null)
                    .status("FAILED")
                    .refundedAmount(BigDecimal.ZERO)
                    .build();
            }

        } catch (Exception e) {
            log.error("Error processing MoMo refund", e);
            return RefundResponse.builder()
                .status("FAILED")
                .refundedAmount(BigDecimal.ZERO)
                .build();
        }
    }

    @Override
    public boolean cancelPayment(Payment payment) {
        // MoMo doesn't support direct payment cancellation
        // Payment will expire automatically after timeout
        log.warn("MoMo payment cancellation not supported, payment will expire: {}", payment.getId());
        return false;
    }

    // ==================== Helper Methods ====================

    private String generateRequestId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestamp + uuid;
    }

    private String buildOrderInfo(Payment payment, InitiatePaymentRequest request) {
        if (payment.getOrderId() != null) {
            return "Thanh toán đơn hàng #" + payment.getOrderId();
        } else if (payment.getCheckoutId() != null) {
            return "Thanh toán checkout #" + payment.getCheckoutId();
        }
        return "Thanh toán đơn hàng";
    }

    private String encodeExtraData(Map<String, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return "";
        }
        try {
            String json = objectMapper.writeValueAsString(metadata);
            return Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.warn("Failed to encode extra data", e);
            return "";
        }
    }

    private String getRequestType(PaymentMethod method) {
        return switch (method) {
            case MOMO, WALLET -> REQUEST_TYPE_CAPTURE_WALLET;
            case ATM_CARD -> REQUEST_TYPE_PAY_WITH_ATM;
            case CREDIT_CARD -> REQUEST_TYPE_PAY_WITH_CC;
            default -> REQUEST_TYPE_CAPTURE_WALLET;
        };
    }

    private String buildRawSignature(String accessKey, long amount, String extraData, String ipnUrl,
                                     String orderId, String orderInfo, String partnerCode,
                                     String redirectUrl, String requestId, String requestType) {
        return String.format(
            "accessKey=%s&amount=%d&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
            accessKey, amount, extraData, ipnUrl, orderId, orderInfo, partnerCode, redirectUrl, requestId, requestType
        );
    }

    private String hmacSHA256(String key, String data) {
        try {
            Mac hmac256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac256.init(secretKeySpec);
            byte[] result = hmac256.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error generating HMAC SHA256", e);
            throw new RuntimeException("Failed to generate signature", e);
        }
    }

    private InitiatedPayment parseCreatePaymentResponse(String responseBody, String paymentId, String requestId) {
        try {
            JsonNode responseNode = objectMapper.readTree(responseBody);
            String resultCode = responseNode.path("resultCode").asText();
            String message = responseNode.path("message").asText();

            if ("0".equals(resultCode)) {
                String payUrl = responseNode.path("payUrl").asText();
                String qrCodeUrl = responseNode.path("qrCodeUrl").asText();
                String deeplink = responseNode.path("deeplink").asText();
                String deeplinkMiniApp = responseNode.path("deeplinkMiniApp").asText();

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("requestId", requestId);
                if (!qrCodeUrl.isEmpty()) {
                    additionalData.put("qrCodeUrl", qrCodeUrl);
                }
                if (!deeplinkMiniApp.isEmpty()) {
                    additionalData.put("deeplinkMiniApp", deeplinkMiniApp);
                }

                return InitiatedPayment.builder()
                    .status("success")
                    .paymentId(paymentId)
                    .redirectUrl(payUrl)
                    .qrCode(qrCodeUrl.isEmpty() ? null : qrCodeUrl)
                    .deepLink(deeplink.isEmpty() ? null : deeplink)
                    .expiresAt(LocalDateTime.now().plusMinutes(PAYMENT_EXPIRY_MINUTES))
                    .additionalData(additionalData)
                    .build();
            } else {
                log.error("MoMo payment creation failed: resultCode={}, message={}", resultCode, message);
                return InitiatedPayment.builder()
                    .status("failed")
                    .paymentId(paymentId)
                    .additionalData(Map.of(
                        "errorCode", resultCode,
                        "errorMessage", message
                    ))
                    .build();
            }
        } catch (Exception e) {
            log.error("Error parsing MoMo response", e);
            return InitiatedPayment.builder()
                .status("failed")
                .paymentId(paymentId)
                .additionalData(Map.of("error", "Failed to parse response"))
                .build();
        }
    }

    private PaymentStatus mapResultCodeToStatus(String resultCode) {
        return switch (resultCode) {
            case "0" -> PaymentStatus.SUCCESS;
            case "9000" -> PaymentStatus.PENDING; // Transaction initialized
            case "1006" -> PaymentStatus.CANCELLED; // User cancelled
            case "1005" -> PaymentStatus.EXPIRED; // Timeout
            default -> PaymentStatus.FAILED;
        };
    }

    private PaymentMethod mapPayTypeToMethod(String payType) {
        if (payType == null) {
            return PaymentMethod.MOMO;
        }
        return switch (payType.toLowerCase()) {
            case "qr", "napas" -> PaymentMethod.ATM_CARD;
            case "credit" -> PaymentMethod.CREDIT_CARD;
            default -> PaymentMethod.MOMO;
        };
    }

    private String getMoMoErrorMessage(String resultCode, String defaultMessage) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("9000", "Giao dịch đang được xử lý");
        errorMessages.put("1001", "Giao dịch thất bại do lỗi kết nối");
        errorMessages.put("1002", "Giao dịch bị từ chối bởi MoMo");
        errorMessages.put("1003", "Giao dịch bị hủy");
        errorMessages.put("1004", "Số tiền giao dịch vượt quá hạn mức thanh toán");
        errorMessages.put("1005", "Giao dịch đã hết hạn");
        errorMessages.put("1006", "Giao dịch đã bị hủy bởi người dùng");
        errorMessages.put("1007", "Giao dịch thất bại do thiếu thông tin");
        errorMessages.put("1017", "Giao dịch bị chặn do không đủ tiền");
        errorMessages.put("1026", "Hạn mức giao dịch bị vượt");
        errorMessages.put("1080", "Hoàn tiền một phần không được hỗ trợ");
        errorMessages.put("1081", "Hoàn tiền thất bại");
        errorMessages.put("2001", "Giao dịch thất bại");
        errorMessages.put("2007", "Giao dịch bị từ chối do nghi ngờ gian lận");
        errorMessages.put("4001", "Giao dịch đang chờ xử lý");
        errorMessages.put("4010", "Đang xác minh OTP");
        errorMessages.put("4011", "Đang chờ người dùng xác nhận thanh toán");
        errorMessages.put("4015", "Giao dịch đang được xử lý bởi đối tác");
        errorMessages.put("4100", "Giao dịch đang được xử lý, vui lòng chờ");
        
        return errorMessages.getOrDefault(resultCode, defaultMessage != null ? defaultMessage : "Giao dịch thất bại");
    }
}
