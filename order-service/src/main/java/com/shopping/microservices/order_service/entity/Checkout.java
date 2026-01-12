package com.shopping.microservices.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "checkout", schema = "order_service_db")
public class Checkout {
    @Id
    @Size(max = 36)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Lob
    @Column(name = "note")
    private String note;

    @Size(max = 100)
    @Column(name = "promotion_code", length = 100)
    private String promotionCode;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 50)
    @Column(name = "progress", length = 50)
    private String progress;

    @Size(max = 50)
    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Size(max = 50)
    @Column(name = "shipment_method_id", length = 50)
    private String shipmentMethodId;

    @Size(max = 50)
    @Column(name = "payment_method_id", length = 50)
    private String paymentMethodId;

    @Column(name = "shipping_address_id")
    private Long shippingAddressId;

    @Column(name = "last_error")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> lastError;

    @Column(name = "attributes")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributes;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @ColumnDefault("0.00")
    @Column(name = "total_shipment_fee", precision = 19, scale = 2)
    private BigDecimal totalShipmentFee;

    @ColumnDefault("0.00")
    @Column(name = "total_shipment_tax", precision = 19, scale = 2)
    private BigDecimal totalShipmentTax;

    @Column(name = "total_tax", precision = 19, scale = 2)
    private BigDecimal totalTax;

    @ColumnDefault("0.00")
    @Column(name = "total_discount_amount", precision = 19, scale = 2)
    private BigDecimal totalDiscountAmount;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}