package com.shopping.microservices.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_item", schema = "order_service_db")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "order_id")
    private Order order;

    @Size(max = 255)
    @Column(name = "name")
    private String name;

    @Size(max = 100)
    @Column(name = "sku", length = 100)
    private String sku;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", precision = 19, scale = 2)
    private BigDecimal price;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "discount_amount", precision = 19, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "tax_percent", precision = 5, scale = 2)
    private BigDecimal taxPercent;

    @Column(name = "shipment_fee", precision = 19, scale = 2)
    private BigDecimal shipmentFee;

    @Size(max = 50)
    @Column(name = "status", length = 50)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "shipment_tax", precision = 19, scale = 2)
    private BigDecimal shipmentTax;

    @Column(name = "processing_state")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> processingState;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}