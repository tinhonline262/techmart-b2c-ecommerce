package com.shopping.microservices.order_service.entity;

import com.shopping.microservices.order_service.enumeration.OrderProgress;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.enumeration.PaymentStatus;
import com.shopping.microservices.order_service.enumeration.ShipmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "`order`", schema = "order_service_db")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "email")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private OrderAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private OrderAddress billingAddress;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Size(max = 50)
    @Column(name = "shipment_method_id", length = 50)
    private String shipmentMethodId;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipment_status", length = 50)
    @Builder.Default
    private ShipmentStatus shipmentStatus = ShipmentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 50)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "progress", length = 50)
    @Builder.Default
    private OrderProgress progress = OrderProgress.CREATED;

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

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }
}