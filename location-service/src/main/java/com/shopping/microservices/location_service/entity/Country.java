package com.shopping.microservices.location_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "country", schema = "location_service_db")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 450)
    @NotNull
    @Column(name = "name", nullable = false, length = 450)
    private String name;

    @Size(max = 3)
    @Column(name = "code2", length = 3)
    private String code2;

    @Size(max = 3)
    @Column(name = "code3", length = 3)
    private String code3;

    @Column(name = "is_billing_enabled")
    private Boolean isBillingEnabled;

    @Column(name = "is_shipping_enabled")
    private Boolean isShippingEnabled;

    @Column(name = "is_city_enabled")
    private Boolean isCityEnabled;

    @Column(name = "is_zip_code_enabled")
    private Boolean isZipCodeEnabled;

    @Column(name = "is_district_enabled")
    private Boolean isDistrictEnabled;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}