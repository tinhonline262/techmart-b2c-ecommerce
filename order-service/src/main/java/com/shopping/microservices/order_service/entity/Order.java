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
@Table(name = "`order`", schema = "order_service_db")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @Lob
    @Column(name = "note")
    private String note;

    @Column(name = "total_tax")
    private Float totalTax;

    @Column(name = "total_discount_amount")
    private Float totalDiscountAmount;

    @Column(name = "number_item")
    private Integer numberItem;

    @Size(max = 100)
    @Column(name = "promotion_code", length = 100)
    private String promotionCode;

    @Column(name = "total_amount", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "total_shipment_fee", precision = 19, scale = 2)
    private BigDecimal totalShipmentFee;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    private String status;

    @Size(max = 50)
    @Column(name = "shipment_method_id", length = 50)
    private String shipmentMethodId;

    @Size(max = 50)
    @Column(name = "shipment_status", length = 50)
    private String shipmentStatus;

    @Size(max = 50)
    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "payment_id")
    private Long paymentId;

    @Size(max = 50)
    @Column(name = "checkout_id", length = 50)
    private String checkoutId;

    @Size(max = 255)
    @Column(name = "reject_reason")
    private String rejectReason;

    @Size(max = 50)
    @Column(name = "payment_method_id", length = 50)
    private String paymentMethodId;

    @Size(max = 50)
    @Column(name = "progress", length = 50)
    private String progress;

    @Size(max = 50)
    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "last_error")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> lastError;

    @Column(name = "attributes")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> attributes;

    @Column(name = "total_shipment_tax", precision = 19, scale = 2)
    private BigDecimal totalShipmentTax;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}