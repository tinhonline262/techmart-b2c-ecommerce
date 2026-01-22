package com.shopping.microservices.common_library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Address DTO for representing shipping and billing addresses.
 * 
 * Designed for Vietnamese address format with support for
 * districts, provinces, and international countries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Contact person's name
     */
    private String contactName;

    /**
     * Contact phone number
     */
    private String phone;

    /**
     * Primary address line (street, building, etc.)
     */
    private String addressLine1;

    /**
     * Secondary address line (apartment, floor, etc.)
     */
    private String addressLine2;

    /**
     * Ward (Phường/Xã)
     */
    private String ward;

    /**
     * Ward ID for API integrations
     */
    private Long wardId;

    /**
     * District ID (Quận/Huyện)
     */
    private Long districtId;

    /**
     * District name (Quận/Huyện)
     */
    private String districtName;

    /**
     * State/Province ID (Tỉnh/Thành phố)
     */
    private Long stateOrProvinceId;

    /**
     * State/Province name (Tỉnh/Thành phố)
     */
    private String stateOrProvinceName;

    /**
     * City name
     */
    private String city;

    /**
     * Postal/ZIP code
     */
    private String zipCode;

    /**
     * Country ID
     */
    private Long countryId;

    /**
     * Country name
     */
    private String countryName;

    /**
     * Country ISO code (e.g., VN, US)
     */
    @Builder.Default
    private String countryCode = "VN";

    /**
     * Whether this is the default address
     */
    @Builder.Default
    private Boolean isDefault = false;

    /**
     * Address type (SHIPPING, BILLING, BOTH)
     */
    private String addressType;

    /**
     * Get formatted full address string
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        
        if (addressLine1 != null) sb.append(addressLine1);
        if (addressLine2 != null) sb.append(", ").append(addressLine2);
        if (ward != null) sb.append(", ").append(ward);
        if (districtName != null) sb.append(", ").append(districtName);
        if (stateOrProvinceName != null) sb.append(", ").append(stateOrProvinceName);
        if (countryName != null) sb.append(", ").append(countryName);
        if (zipCode != null) sb.append(" ").append(zipCode);
        
        return sb.toString();
    }
}
