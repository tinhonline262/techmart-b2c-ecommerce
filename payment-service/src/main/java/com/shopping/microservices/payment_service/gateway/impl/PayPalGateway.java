package com.shopping.microservices.payment_service.gateway.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PayPal Payment Gateway implementation using PayPal REST API (Orders v2).
 * 
 * Features:
 * - OAuth 2.0 authentication with token caching
 * - Multi-currency support with VND to USD conversion
 * - Order creation and capture flow
 * - Webhook signature verification
 * - Refund processing
 */
@Service("PAYPAL")
@Slf4j
public class PayPalGateway implements PaymentGateway {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${payment.paypal.client-id}")
    private String clientId;

    @Value("${payment.paypal.client-secret}")
    private String clientSecret;

    @Value("${payment.paypal.api-url}")
    private String apiUrl;

    @Value("${payment.paypal.return-url}")
    private String returnUrl;

    @Value("${payment.paypal.cancel-url}")
    private String cancelUrl;

    @Value("${payment.paypal.webhook-id:}")
    private String webhookId;

    // Currency conversion rate (VND to USD) - In production, fetch from exchange rate API
    @Value("${payment.paypal.vnd-to-usd-rate:24000}")
    private BigDecimal vndToUsdRate;

    // Supported currencies by PayPal (VND is NOT supported)
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of(
        "USD", "EUR", "GBP", "AUD", "CAD", "JPY", "SGD"
    );

    // PayPal API endpoints
    private static final String OAUTH_TOKEN_ENDPOINT = "/v1/oauth2/token";
    private static final String ORDERS_ENDPOINT = "/v2/checkout/orders";
    private static final String REFUND_ENDPOINT = "/v2/payments/captures/%s/refund";

    // Token cache
    private final Map<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();
    private static final String TOKEN_CACHE_KEY = "paypal_access_token";

    // Payment link expiration (3 hours for PayPal)
    private static final int PAYMENT_EXPIRY_HOURS = 3;

    public PayPalGateway(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public String getProviderId() {
        return "PAYPAL";
    }

    @Override
    public List<PaymentMethod> getSupportedMethods() {
        return Arrays.asList(
            PaymentMethod.PAYPAL,
            PaymentMethod.CREDIT_CARD // PayPal supports credit/debit cards
        );
    }

    @Override
    public boolean supports(PaymentMethod method) {
        return getSupportedMethods().contains(method);
    }

    @Override
    public InitiatedPayment initiatePayment(Payment payment, InitiatePaymentRequest request) {
        log.info("Initiating PayPal payment for payment ID: {}, amount: {}", 
            payment.getId(), payment.getTotalAmount());

        try {
            // Get access token
            String accessToken = getAccessToken();

            // Convert VND to USD
            CurrencyConversion conversion = convertToUSD(payment.getTotalAmount());

            // Build order request
            ObjectNode orderRequest = buildOrderRequest(payment, request, conversion);

            // Create PayPal order
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("PayPal-Request-Id", generateRequestId(payment));

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(orderRequest), headers);

            log.debug("Creating PayPal order for payment: {}", payment.getId());

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + ORDERS_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );

            return parseOrderResponse(response.getBody(), payment.getId().toString(), conversion);

        } catch (Exception e) {
            log.error("Error initiating PayPal payment for payment ID: {}", payment.getId(), e);
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
            // For PayPal webhooks, verify using transmission signature
            String transmissionId = params.get("PAYPAL-TRANSMISSION-ID");
            String transmissionTime = params.get("PAYPAL-TRANSMISSION-TIME");
            String transmissionSig = params.get("PAYPAL-TRANSMISSION-SIG");
            String certUrl = params.get("PAYPAL-CERT-URL");
            String authAlgo = params.get("PAYPAL-AUTH-ALGO");
            String webhookBody = params.get("WEBHOOK_BODY");

            if (transmissionId == null || transmissionSig == null) {
                log.warn("PayPal webhook missing signature headers");
                return false;
            }

            // In production, verify webhook signature using PayPal API
            // POST /v1/notifications/verify-webhook-signature
            // For now, we'll do basic validation

            if (webhookId == null || webhookId.isEmpty()) {
                log.warn("PayPal webhook ID not configured, skipping signature verification");
                return true; // Skip verification if not configured
            }

            String accessToken = getAccessToken();
            return verifyWebhookSignature(accessToken, transmissionId, transmissionTime,
                transmissionSig, certUrl, authAlgo, webhookBody);

        } catch (Exception e) {
            log.error("Error verifying PayPal webhook", e);
            return false;
        }
    }

    @Override
    public CapturedPayment processCallback(Map<String, String> params) {
        log.info("Processing PayPal callback");

        try {
            String eventType = params.get("event_type");
            String resourceId = params.get("resource_id");
            String orderId = params.get("order_id");
            String captureId = params.get("capture_id");
            String amount = params.get("amount");
            String currency = params.get("currency");

            PaymentStatus status = mapEventTypeToStatus(eventType);
            String failureMessage = null;

            if (status == PaymentStatus.FAILED) {
                failureMessage = params.getOrDefault("failure_reason", "Payment failed");
            }

            // Convert amount back to VND if in USD
            BigDecimal paymentAmount = new BigDecimal(amount != null ? amount : "0");
            if ("USD".equals(currency)) {
                paymentAmount = paymentAmount.multiply(vndToUsdRate).setScale(0, RoundingMode.HALF_UP);
            }

            return CapturedPayment.builder()
                .orderId(null) // Will be set from payment record
                .amount(paymentAmount)
                .gatewayTransactionId(captureId != null ? captureId : resourceId)
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(status)
                .failureMessage(failureMessage)
                .metadata(Map.of(
                    "paypalOrderId", orderId != null ? orderId : "",
                    "paypalCaptureId", captureId != null ? captureId : "",
                    "originalCurrency", currency != null ? currency : "USD",
                    "originalAmount", amount != null ? amount : "0"
                ))
                .build();

        } catch (Exception e) {
            log.error("Error processing PayPal callback", e);
            return CapturedPayment.builder()
                .paymentStatus(PaymentStatus.FAILED)
                .failureMessage("Failed to process PayPal callback: " + e.getMessage())
                .build();
        }
    }

    @Override
    public PaymentStatus queryPaymentStatus(String transactionId) {
        log.info("Querying PayPal order status: {}", transactionId);

        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + ORDERS_ENDPOINT + "/" + transactionId,
                HttpMethod.GET,
                entity,
                String.class
            );

            JsonNode orderNode = objectMapper.readTree(response.getBody());
            String status = orderNode.path("status").asText();

            return mapPayPalStatusToPaymentStatus(status);

        } catch (Exception e) {
            log.error("Error querying PayPal order status", e);
            return null;
        }
    }

    @Override
    public RefundResponse processRefund(Payment payment, BigDecimal amount, String reason) {
        log.info("Processing PayPal refund for payment: {}, amount: {}", payment.getId(), amount);

        try {
            String accessToken = getAccessToken();
            String captureId = payment.getGatewayTransactionId();

            // Convert amount to USD
            CurrencyConversion conversion = convertToUSD(amount);

            ObjectNode refundRequest = objectMapper.createObjectNode();
            ObjectNode amountNode = objectMapper.createObjectNode();
            amountNode.put("value", conversion.getConvertedAmount().toPlainString());
            amountNode.put("currency_code", "USD");
            refundRequest.set("amount", amountNode);
            refundRequest.put("note_to_payer", reason);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("PayPal-Request-Id", "REFUND_" + payment.getId() + "_" + System.currentTimeMillis());

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(refundRequest), headers);

            String refundUrl = apiUrl + String.format(REFUND_ENDPOINT, captureId);
            ResponseEntity<String> response = restTemplate.exchange(
                refundUrl,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode refundNode = objectMapper.readTree(response.getBody());
            String refundId = refundNode.path("id").asText();
            String status = refundNode.path("status").asText();

            return RefundResponse.builder()
                .refundId(refundId)
                .status("COMPLETED".equals(status) ? "SUCCESS" : status)
                .refundedAmount(amount)
                .refundedAt(LocalDateTime.now())
                .build();

        } catch (Exception e) {
            log.error("Error processing PayPal refund", e);
            return RefundResponse.builder()
                .status("FAILED")
                .refundedAmount(BigDecimal.ZERO)
                .build();
        }
    }

    @Override
    public boolean cancelPayment(Payment payment) {
        // PayPal orders expire automatically if not captured
        // We can void an authorized payment, but for CAPTURE intent, cancellation isn't needed
        log.info("PayPal payment cancellation requested for: {}", payment.getId());
        return true;
    }

    /**
     * Capture a PayPal order after customer approval
     */
    public CapturedPayment captureOrder(String orderId) {
        log.info("Capturing PayPal order: {}", orderId);

        try {
            String accessToken = getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            headers.set("PayPal-Request-Id", "CAPTURE_" + orderId + "_" + System.currentTimeMillis());

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + ORDERS_ENDPOINT + "/" + orderId + "/capture",
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode captureNode = objectMapper.readTree(response.getBody());
            String status = captureNode.path("status").asText();

            if ("COMPLETED".equals(status)) {
                JsonNode purchaseUnit = captureNode.path("purchase_units").get(0);
                JsonNode capture = purchaseUnit.path("payments").path("captures").get(0);
                
                String captureId = capture.path("id").asText();
                String amount = capture.path("amount").path("value").asText();
                String currency = capture.path("amount").path("currency_code").asText();

                // Convert back to VND
                BigDecimal capturedAmount = new BigDecimal(amount);
                if ("USD".equals(currency)) {
                    capturedAmount = capturedAmount.multiply(vndToUsdRate).setScale(0, RoundingMode.HALF_UP);
                }

                return CapturedPayment.builder()
                    .amount(capturedAmount)
                    .gatewayTransactionId(captureId)
                    .paymentMethod(PaymentMethod.PAYPAL)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .metadata(Map.of(
                        "paypalOrderId", orderId,
                        "paypalCaptureId", captureId
                    ))
                    .build();
            } else {
                return CapturedPayment.builder()
                    .paymentStatus(PaymentStatus.FAILED)
                    .failureMessage("PayPal order capture failed with status: " + status)
                    .build();
            }

        } catch (Exception e) {
            log.error("Error capturing PayPal order", e);
            return CapturedPayment.builder()
                .paymentStatus(PaymentStatus.FAILED)
                .failureMessage("Failed to capture PayPal order: " + e.getMessage())
                .build();
        }
    }

    // ==================== OAuth Token Management ====================

    private String getAccessToken() {
        TokenInfo cachedToken = tokenCache.get(TOKEN_CACHE_KEY);
        if (cachedToken != null && !cachedToken.isExpired()) {
            return cachedToken.getAccessToken();
        }

        log.debug("Fetching new PayPal access token");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientId, clientSecret);

            HttpEntity<String> entity = new HttpEntity<>("grant_type=client_credentials", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + OAUTH_TOKEN_ENDPOINT,
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode tokenNode = objectMapper.readTree(response.getBody());
            String accessToken = tokenNode.path("access_token").asText();
            int expiresIn = tokenNode.path("expires_in").asInt();

            TokenInfo tokenInfo = new TokenInfo(accessToken, expiresIn);
            tokenCache.put(TOKEN_CACHE_KEY, tokenInfo);

            log.info("Successfully obtained PayPal access token, expires in {} seconds", expiresIn);
            return accessToken;

        } catch (Exception e) {
            log.error("Failed to get PayPal access token", e);
            throw new RuntimeException("Failed to authenticate with PayPal", e);
        }
    }

    // ==================== Helper Methods ====================

    private String generateRequestId(Payment payment) {
        return "ORDER_" + payment.getId() + "_" + System.currentTimeMillis();
    }

    private CurrencyConversion convertToUSD(BigDecimal amountVND) {
        BigDecimal amountUSD = amountVND.divide(vndToUsdRate, 2, RoundingMode.HALF_UP);
        return new CurrencyConversion("VND", "USD", amountVND, amountUSD, vndToUsdRate);
    }

    private ObjectNode buildOrderRequest(Payment payment, InitiatePaymentRequest request, CurrencyConversion conversion) {
        ObjectNode orderRequest = objectMapper.createObjectNode();
        orderRequest.put("intent", "CAPTURE");

        // Purchase units
        ArrayNode purchaseUnits = objectMapper.createArrayNode();
        ObjectNode purchaseUnit = objectMapper.createObjectNode();
        purchaseUnit.put("reference_id", payment.getId().toString());
        purchaseUnit.put("description", "Payment for order #" + 
            (payment.getOrderId() != null ? payment.getOrderId() : payment.getCheckoutId()));

        // Amount
        ObjectNode amount = objectMapper.createObjectNode();
        amount.put("currency_code", "USD");
        amount.put("value", conversion.getConvertedAmount().toPlainString());

        // Breakdown
        ObjectNode breakdown = objectMapper.createObjectNode();
        ObjectNode itemTotal = objectMapper.createObjectNode();
        itemTotal.put("currency_code", "USD");
        itemTotal.put("value", conversion.getConvertedAmount().toPlainString());
        breakdown.set("item_total", itemTotal);
        amount.set("breakdown", breakdown);

        purchaseUnit.set("amount", amount);

        // Items (if provided)
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            ArrayNode items = objectMapper.createArrayNode();
            for (InitiatePaymentRequest.PaymentItem item : request.getItems()) {
                ObjectNode itemNode = objectMapper.createObjectNode();
                itemNode.put("name", item.getName());
                itemNode.put("quantity", item.getQuantity().toString());
                
                ObjectNode unitAmount = objectMapper.createObjectNode();
                BigDecimal itemPriceUSD = item.getPrice().divide(vndToUsdRate, 2, RoundingMode.HALF_UP);
                unitAmount.put("currency_code", "USD");
                unitAmount.put("value", itemPriceUSD.toPlainString());
                itemNode.set("unit_amount", unitAmount);
                
                items.add(itemNode);
            }
            purchaseUnit.set("items", items);
        }

        // Custom ID for reference
        ObjectNode customId = objectMapper.createObjectNode();
        purchaseUnit.put("custom_id", payment.getId().toString());

        purchaseUnits.add(purchaseUnit);
        orderRequest.set("purchase_units", purchaseUnits);

        // Application context
        ObjectNode applicationContext = objectMapper.createObjectNode();
        applicationContext.put("brand_name", "Online Shopping");
        applicationContext.put("landing_page", "LOGIN");
        applicationContext.put("user_action", "PAY_NOW");
        applicationContext.put("return_url", request.getReturnUrl() != null ? request.getReturnUrl() : returnUrl);
        applicationContext.put("cancel_url", request.getCancelUrl() != null ? request.getCancelUrl() : cancelUrl);
        orderRequest.set("application_context", applicationContext);

        return orderRequest;
    }

    private InitiatedPayment parseOrderResponse(String responseBody, String paymentId, CurrencyConversion conversion) {
        try {
            JsonNode orderNode = objectMapper.readTree(responseBody);
            String orderId = orderNode.path("id").asText();
            String status = orderNode.path("status").asText();

            if ("CREATED".equals(status) || "PAYER_ACTION_REQUIRED".equals(status)) {
                // Find approval URL
                String approvalUrl = null;
                JsonNode links = orderNode.path("links");
                for (JsonNode link : links) {
                    if ("approve".equals(link.path("rel").asText()) || 
                        "payer-action".equals(link.path("rel").asText())) {
                        approvalUrl = link.path("href").asText();
                        break;
                    }
                }

                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("paypalOrderId", orderId);
                additionalData.put("originalAmount", conversion.getOriginalAmount());
                additionalData.put("convertedAmount", conversion.getConvertedAmount());
                additionalData.put("exchangeRate", conversion.getExchangeRate());
                additionalData.put("originalCurrency", conversion.getOriginalCurrency());
                additionalData.put("convertedCurrency", conversion.getTargetCurrency());

                return InitiatedPayment.builder()
                    .status("success")
                    .paymentId(paymentId)
                    .redirectUrl(approvalUrl)
                    .expiresAt(LocalDateTime.now().plusHours(PAYMENT_EXPIRY_HOURS))
                    .additionalData(additionalData)
                    .build();
            } else {
                return InitiatedPayment.builder()
                    .status("failed")
                    .paymentId(paymentId)
                    .additionalData(Map.of("error", "Order creation failed with status: " + status))
                    .build();
            }

        } catch (Exception e) {
            log.error("Error parsing PayPal order response", e);
            return InitiatedPayment.builder()
                .status("failed")
                .paymentId(paymentId)
                .additionalData(Map.of("error", "Failed to parse PayPal response"))
                .build();
        }
    }

    private boolean verifyWebhookSignature(String accessToken, String transmissionId, String transmissionTime,
                                           String transmissionSig, String certUrl, String authAlgo, String webhookBody) {
        try {
            ObjectNode verifyRequest = objectMapper.createObjectNode();
            verifyRequest.put("transmission_id", transmissionId);
            verifyRequest.put("transmission_time", transmissionTime);
            verifyRequest.put("cert_url", certUrl);
            verifyRequest.put("auth_algo", authAlgo);
            verifyRequest.put("transmission_sig", transmissionSig);
            verifyRequest.put("webhook_id", webhookId);
            verifyRequest.put("webhook_event", webhookBody);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(verifyRequest), headers);

            ResponseEntity<String> response = restTemplate.exchange(
                apiUrl + "/v1/notifications/verify-webhook-signature",
                HttpMethod.POST,
                entity,
                String.class
            );

            JsonNode responseNode = objectMapper.readTree(response.getBody());
            String verificationStatus = responseNode.path("verification_status").asText();

            return "SUCCESS".equals(verificationStatus);

        } catch (Exception e) {
            log.error("Error verifying PayPal webhook signature", e);
            return false;
        }
    }

    private PaymentStatus mapEventTypeToStatus(String eventType) {
        if (eventType == null) {
            return PaymentStatus.PENDING;
        }
        return switch (eventType) {
            case "PAYMENT.CAPTURE.COMPLETED" -> PaymentStatus.SUCCESS;
            case "PAYMENT.CAPTURE.DENIED" -> PaymentStatus.FAILED;
            case "PAYMENT.CAPTURE.PENDING" -> PaymentStatus.PROCESSING;
            case "PAYMENT.CAPTURE.REFUNDED" -> PaymentStatus.REFUNDED;
            case "CHECKOUT.ORDER.APPROVED" -> PaymentStatus.PROCESSING; // Ready for capture
            default -> PaymentStatus.PENDING;
        };
    }

    private PaymentStatus mapPayPalStatusToPaymentStatus(String status) {
        return switch (status) {
            case "COMPLETED" -> PaymentStatus.SUCCESS;
            case "APPROVED" -> PaymentStatus.PROCESSING;
            case "VOIDED" -> PaymentStatus.CANCELLED;
            case "PAYER_ACTION_REQUIRED", "CREATED" -> PaymentStatus.INITIATED;
            default -> PaymentStatus.PENDING;
        };
    }

    // ==================== Inner Classes ====================

    private static class TokenInfo {
        private final String accessToken;
        private final Instant expiresAt;

        public TokenInfo(String accessToken, int expiresInSeconds) {
            this.accessToken = accessToken;
            // Expire 5 minutes early to be safe
            this.expiresAt = Instant.now().plusSeconds(expiresInSeconds - 300);
        }

        public String getAccessToken() {
            return accessToken;
        }

        public boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }

    public static class CurrencyConversion {
        private final String originalCurrency;
        private final String targetCurrency;
        private final BigDecimal originalAmount;
        private final BigDecimal convertedAmount;
        private final BigDecimal exchangeRate;

        public CurrencyConversion(String originalCurrency, String targetCurrency, 
                                  BigDecimal originalAmount, BigDecimal convertedAmount, BigDecimal exchangeRate) {
            this.originalCurrency = originalCurrency;
            this.targetCurrency = targetCurrency;
            this.originalAmount = originalAmount;
            this.convertedAmount = convertedAmount;
            this.exchangeRate = exchangeRate;
        }

        public String getOriginalCurrency() { return originalCurrency; }
        public String getTargetCurrency() { return targetCurrency; }
        public BigDecimal getOriginalAmount() { return originalAmount; }
        public BigDecimal getConvertedAmount() { return convertedAmount; }
        public BigDecimal getExchangeRate() { return exchangeRate; }
    }
}
