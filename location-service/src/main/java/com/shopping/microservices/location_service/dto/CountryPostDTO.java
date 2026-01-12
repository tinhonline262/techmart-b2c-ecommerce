package com.shopping.microservices.location_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CountryPostDTO(
        @NotBlank(message = "Country name is required")
        @Size(max = 255, message = "Country name must not exceed 255 characters")
        String name,

        @Size(max = 2, message = "Code2 must be 2 characters")
        String code2,

        @Size(max = 3, message = "Code3 must be 3 characters")
        String code3,

        Boolean isBillingEnabled,
        Boolean isShippingEnabled,
        Boolean isCityEnabled,
        Boolean isZipCodeEnabled,
        Boolean isDistrictEnabled
) {
}
