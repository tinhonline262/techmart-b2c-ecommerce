package com.shopping.microservices.product_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category", schema = "product_service_db")
public class Category {
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
    @Column(name = "description")
    private String description;

    @ColumnDefault("0")
    @Column(name = "display_order")
    private Integer displayOrder;

    @Size(max = 255)
    @Column(name = "meta_keyword")
    private String metaKeyword;

    @Lob
    @Column(name = "meta_description")
    private String metaDescription;

    @ColumnDefault("0")
    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "image_id")
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

}