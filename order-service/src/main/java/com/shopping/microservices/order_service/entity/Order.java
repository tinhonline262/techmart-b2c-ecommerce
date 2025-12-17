package com.shopping.microservices.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "orders", schema = "order_service_db")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "order_number", nullable = false)
    private String orderNumber;

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Size(max = 255)
    @NotNull
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Size(max = 255)
    @NotNull
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @CreatedDate
    @Column(name = "order_date")
    private Instant orderDate;

    @Size(max = 50)
    @NotNull
    @Builder.Default
    @Column(name = "status", nullable = false, length = 50)
    private String status = "PENDING";

    @NotNull
    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @CreatedDate
    @Column(name = "created_at")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems = new LinkedHashSet<>();

    @OneToMany(mappedBy = "order")
    private Set<OrderStatusHistory> orderStatusHistories = new LinkedHashSet<>();

}