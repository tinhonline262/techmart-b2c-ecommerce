package com.shopping.microservices.notification_service.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for sending order notification
 */
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderSendNotificationEvent(String orderNumber, String customerName, String customerEmail, Instant orderDate, String status, BigDecimal totalAmount) implements Serializable {

}