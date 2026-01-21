package com.shopping.microservices.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_option_value", schema = "product_service_db")
public class ProductAttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductAttribute productAttribute;

    @Size(max = 255)
    @NotNull
    @Column(name = "value", nullable = false)
    private String value;

    @Size(max = 50)
    @Column(name = "display_type", length = 50)
    private String displayType;

    @ColumnDefault("0")
    @Column(name = "display_order")
    private Integer displayOrder;

}
