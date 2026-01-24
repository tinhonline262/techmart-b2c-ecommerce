package com.shopping.microservices.payment_service.gateway.impl;

import com.shopping.microservices.payment_service.dto.CapturedPayment;
import com.shopping.microservices.payment_service.dto.InitiatePaymentRequest;
import com.shopping.microservices.payment_service.dto.InitiatedPayment;
import com.shopping.microservices.payment_service.dto.RefundResponse;
import com.shopping.microservices.payment_service.entity.Payment;
import com.shopping.microservices.payment_service.enums.PaymentMethod;
import com.shopping.microservices.payment_service.enums.PaymentStatus;
import com.shopping.microservices.payment_service.gateway.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service("VNPAY")
@Slf4j
@RequiredArgsConstructor
public class VNPayGateway implements PaymentGateway {
    
    @Value("${payment.vnpay.tmn-code}")
    private String tmnCode;
    
    @Value("${payment.vnpay.hash-secret}")
    private String hashSecret;
    
    @Value("${payment.vnpay.api-url}")
    private String apiUrl;
    
    @Value("${payment.vnpay.return-url}")
    private String returnUrl;
    
    private static final String VNP_VERSION = "2.1.0";
    private static final String VNP_COMMAND = "pay";
    private static final String VNP_CURR_CODE = "VND";
    
    @Override
    public String getProviderId() {
        return "VNPAY";
    }
    
    @Override
    public List<PaymentMethod> getSupportedMethods() {
        return Arrays.asList(
            PaymentMethod.VNPAY,
            PaymentMethod.BANK_TRANSFER,
            PaymentMethod.ATM_CARD,
            PaymentMethod.CREDIT_CARD
        );
    }
    
    @Override
    public boolean supports(PaymentMethod method) {
        return getSupportedMethods().contains(method);
    }
    
    @Override
    public InitiatedPayment initiatePayment(Payment payment, InitiatePaymentRequest request) {
        log.info("Initiating VNPay payment for payment ID: {}", payment.getId());
        
        try {
            Map<String, String> vnpParams = new TreeMap<>();
            
            // Build VNPay parameters
            vnpParams.put("vnp_Version", VNP_VERSION);
            vnpParams.put("vnp_Command", VNP_COMMAND);
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(payment.getTotalAmount().multiply(new BigDecimal(100)).longValue()));
            vnpParams.put("vnp_CurrCode", VNP_CURR_CODE);
            vnpParams.put("vnp_TxnRef", payment.getId().toString());
            vnpParams.put("vnp_OrderInfo", "Payment for order: " + (payment.getOrderId() != null ? payment.getOrderId() : payment.getCheckoutId()));
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", request.getLocale() != null ? request.getLocale() : "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", request.getCustomerInfo() != null ? request.getCustomerInfo().getIpAddress() : "127.0.0.1");
            vnpParams.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            
            // Add bank code if payment method is specific
            if (payment.getPaymentMethod() == PaymentMethod.ATM_CARD) {
                vnpParams.put("vnp_BankCode", "VNBANK");
            } else if (payment.getPaymentMethod() == PaymentMethod.CREDIT_CARD) {
                vnpParams.put("vnp_BankCode", "INTCARD");
            }
            
            // Generate secure hash
            String hashData = buildHashData(vnpParams);
            String vnpSecureHash = hmacSHA512(hashSecret, hashData);
            vnpParams.put("vnp_SecureHash", vnpSecureHash);
            
            // Build payment URL
            String paymentUrl = apiUrl + "?" + buildQueryString(vnpParams);
            
            log.info("VNPay payment URL generated for payment: {}", payment.getId());
            
            return InitiatedPayment.builder()
                .status("success")
                .paymentId(payment.getId().toString())
                .redirectUrl(paymentUrl)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
                
        } catch (Exception e) {
            log.error("Error initiating VNPay payment", e);
            return InitiatedPayment.builder()
                .status("failed")
                .paymentId(payment.getId().toString())
                .build();
        }
    }
    
    @Override
    public boolean verifyCallback(Map<String, String> params) {
        try {
            String vnpSecureHash = params.get("vnp_SecureHash");
            if (vnpSecureHash == null) {
                return false;
            }
            
            // Remove hash from params for verification
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("vnp_SecureHash");
            verifyParams.remove("vnp_SecureHashType");
            
            String hashData = buildHashData(verifyParams);
            String calculatedHash = hmacSHA512(hashSecret, hashData);
            
            return vnpSecureHash.equals(calculatedHash);
            
        } catch (Exception e) {
            log.error("Error verifying VNPay callback", e);
            return false;
        }
    }
    
    @Override
    public CapturedPayment processCallback(Map<String, String> params) {
        log.info("Processing VNPay callback");
        
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionNo = params.get("vnp_TransactionNo");
        String amount = params.get("vnp_Amount");
        
        PaymentStatus status = "00".equals(responseCode) ? 
            PaymentStatus.SUCCESS : PaymentStatus.FAILED;
        
        String failureMessage = null;
        if (!"00".equals(responseCode)) {
            failureMessage = getVNPayErrorMessage(responseCode);
        }
        
        return CapturedPayment.builder()
            .orderId(null) // Will be set from payment record
            .checkoutId(null)
            .amount(new BigDecimal(amount).divide(new BigDecimal(100)))
            .gatewayTransactionId(transactionNo)
            .paymentMethod(PaymentMethod.VNPAY)
            .paymentStatus(status)
            .failureMessage(failureMessage)
            .build();
    }
    
    @Override
    public PaymentStatus queryPaymentStatus(String transactionId) {
        // Implement VNPay query API call
        // For now, return null to indicate not implemented
        return null;
    }
    
    @Override
    public RefundResponse processRefund(Payment payment, BigDecimal amount, String reason) {
        // Implement VNPay refund API
        log.warn("VNPay refund not implemented yet");
        throw new UnsupportedOperationException("Refund not supported for VNPay");
    }
    
    @Override
    public boolean cancelPayment(Payment payment) {
        // VNPay doesn't support programmatic cancellation
        return false;
    }
    
    // Helper methods
    private String buildHashData(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                        .append("&");
            }
        }
        sb.setLength(sb.length() - 1); // remove last &
        return sb.toString();
    }


    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
            .map(e -> {
                try {
                    return e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8.toString());
                } catch (UnsupportedEncodingException ex) {
                    return e.getKey() + "=" + e.getValue();
                }
            })
            .collect(Collectors.joining("&"));
    }
    
    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Error generating HMAC", e);
            return "";
        }
    }
    
    private String getVNPayErrorMessage(String responseCode) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        errorMessages.put("09", "Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        errorMessages.put("10", "Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        errorMessages.put("11", "Đã hết hạn chờ thanh toán");
        errorMessages.put("12", "Thẻ/Tài khoản của khách hàng bị khóa.");
        errorMessages.put("13", "Quý khách nhập sai mật khẩu xác thực giao dịch (OTP)");
        errorMessages.put("24", "Khách hàng hủy giao dịch");
        errorMessages.put("51", "Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        errorMessages.put("65", "Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        errorMessages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        errorMessages.put("79", "KH nhập sai mật khẩu thanh toán quá số lần quy định.");
        errorMessages.put("99", "Các lỗi khác");
        
        return errorMessages.getOrDefault(responseCode, "Giao dịch thất bại");
    }
}
