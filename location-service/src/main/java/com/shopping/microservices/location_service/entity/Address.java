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
@Table(name = "address", schema = "location_service_db")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 450)
    @Column(name = "contact_name", length = 450)
    private String contactName;

    @Size(max = 25)
    @Column(name = "phone", length = 25)
    private String phone;

    @Size(max = 450)
    @Column(name = "address_line_1", length = 450)
    private String addressLine1;

    @Size(max = 450)
    @Column(name = "address_line_2", length = 450)
    private String addressLine2;

    @Size(max = 450)
    @Column(name = "city", length = 450)
    private String city;

    @Size(max = 25)
    @Column(name = "zip_code", length = 25)
    private String zipCode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    // THÊM 2 FIELDS NÀY
    @Column(name = "state_or_province_id")
    private Long stateOrProvinceId;

    @Column(name = "district_id")
    private Long districtId;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;
}