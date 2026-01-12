package com.shopping.microservices.order_service.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record OrderAddressRequest(
        @NotBlank(message = "Contact name is required")
        @Size(max = 255, message = "Contact name cannot exceed 255 characters")
        String contactName,

        @NotBlank(message = "Phone is required")
        @Size(max = 50, message = "Phone cannot exceed 50 characters")
        String phone,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
        String addressLine1,

        @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
        String addressLine2,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @Size(max = 20, message = "Zip code cannot exceed 20 characters")
        String zipCode,

        Long districtId,

        @Size(max = 100, message = "District name cannot exceed 100 characters")
        String districtName,

        Long stateOrProvinceId,

        @Size(max = 100, message = "State/Province name cannot exceed 100 characters")
        String stateOrProvinceName,

        Long countryId,

        @Size(max = 100, message = "Country name cannot exceed 100 characters")
        String countryName
) {
}
