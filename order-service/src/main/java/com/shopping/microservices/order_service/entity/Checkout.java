package com.shopping.microservices.order_service.entity;

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
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CheckoutItem> items = new ArrayList<>();

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

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public void addItem(CheckoutItem item) {
        items.add(item);
        item.setCheckout(this);
    }

    public void removeItem(CheckoutItem item) {
        items.remove(item);
        item.setCheckout(null);
    }

    public void calculateTotals() {
        BigDecimal itemsTotal = BigDecimal.ZERO;
        BigDecimal itemsTax = BigDecimal.ZERO;
        BigDecimal itemsShipmentFee = BigDecimal.ZERO;
        BigDecimal itemsShipmentTax = BigDecimal.ZERO;
        BigDecimal itemsDiscount = BigDecimal.ZERO;

        for (CheckoutItem item : items) {
            if (item.getPrice() != null && item.getQuantity() != null) {
                itemsTotal = itemsTotal.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            if (item.getTax() != null) {
                itemsTax = itemsTax.add(item.getTax());
            }
            if (item.getShipmentFee() != null) {
                itemsShipmentFee = itemsShipmentFee.add(item.getShipmentFee());
            }
            if (item.getShipmentTax() != null) {
                itemsShipmentTax = itemsShipmentTax.add(item.getShipmentTax());
            }
            if (item.getDiscountAmount() != null) {
                itemsDiscount = itemsDiscount.add(item.getDiscountAmount());
            }
        }

        this.totalTax = itemsTax;
        this.totalShipmentFee = itemsShipmentFee;
        this.totalShipmentTax = itemsShipmentTax;
        this.totalDiscountAmount = itemsDiscount;
        this.totalAmount = itemsTotal.add(itemsTax).add(itemsShipmentFee).add(itemsShipmentTax).subtract(itemsDiscount);
    }

}