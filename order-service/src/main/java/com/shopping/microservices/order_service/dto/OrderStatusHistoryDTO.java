package com.shopping.microservices.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.shopping.microservices.order_service.entity.OrderStatusHistory;

import java.io.Serializable;

/**
 * DTO for {@link OrderStatusHistory}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderStatusHistoryDTO(Long id, String status, String comments) implements Serializable {
}