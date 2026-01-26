package com.shopping.microservices.order_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_address", schema = "order_service_db")
public class OrderAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 255)
    @Column(name = "contact_name")
    private String contactName;

    @Size(max = 50)
    @Column(name = "phone", length = 50)
    private String phone;

    @Size(max = 255)
    @Column(name = "address_line1")
    private String addressLine1;

    @Size(max = 255)
    @Column(name = "address_line2")
    private String addressLine2;

    @Size(max = 100)
    @Column(name = "city", length = 100)
    private String city;

    @Size(max = 20)
    @Column(name = "zip_code", length = 20)
    private String zipCode;

    @Column(name = "district_id")
    private Long districtId;

    @Size(max = 100)
    @Column(name = "district_name", length = 100)
    private String districtName;

    @Column(name = "state_or_province_id")
    private Long stateOrProvinceId;

    @Size(max = 100)
    @Column(name = "state_or_province_name", length = 100)
    private String stateOrProvinceName;

    @Column(name = "country_id")
    private Long countryId;

    @Size(max = 100)
    @Column(name = "country_name", length = 100)
    private String countryName;
}