package com.shopping.microservices.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product", schema = "product_service_db")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "slug")
    private String slug;

    @Lob
    @Column(name = "short_description")
    private String shortDescription;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "specification")
    private String specification;

    @Size(max = 100)
    @Column(name = "sku", length = 100)
    private String sku;

    @Size(max = 100)
    @Column(name = "gtin", length = 100)
    private String gtin;

    @NotNull
    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @ColumnDefault("0")
    @Column(name = "has_options")
    private Boolean hasOptions;

    @ColumnDefault("1")
    @Column(name = "is_allowed_to_order")
    private Boolean isAllowedToOrder;

    @ColumnDefault("0")
    @Column(name = "is_published")
    private Boolean isPublished;

    @ColumnDefault("0")
    @Column(name = "is_featured")
    private Boolean isFeatured;

    @ColumnDefault("1")
    @Column(name = "is_visible_individually")
    private Boolean isVisibleIndividually;

    @ColumnDefault("1")
    @Column(name = "stock_tracking_enabled")
    private Boolean stockTrackingEnabled;

    @ColumnDefault("0")
    @Column(name = "stock_quantity")
    private Long stockQuantity;

    @Column(name = "tax_class_id")
    private Long taxClassId;

    @Size(max = 255)
    @Column(name = "meta_title")
    private String metaTitle;

    @Size(max = 255)
    @Column(name = "meta_keyword")
    private String metaKeyword;

    @Lob
    @Column(name = "meta_description")
    private String metaDescription;

    @Column(name = "thumbnail_media_id")
    private Long thumbnailMediaId;

    @Column(name = "weight", precision = 10, scale = 2)
    private BigDecimal weight;

    @Size(max = 50)
    @Column(name = "dimension_unit", length = 50)
    private String dimensionUnit;

    @Column(name = "length", precision = 10, scale = 2)
    private BigDecimal length;

    @Column(name = "width", precision = 10, scale = 2)
    private BigDecimal width;

    @Column(name = "height", precision = 10, scale = 2)
    private BigDecimal height;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "template_id")
    private Long templateId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}