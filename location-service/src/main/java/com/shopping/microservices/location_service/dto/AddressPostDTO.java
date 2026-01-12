package com.shopping.microservices.location_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressPostDTO(
        @NotBlank(message = "Contact name is required")
        @Size(max = 255, message = "Contact name must not exceed 255 characters")
        String contactName,

        @NotBlank(message = "Phone is required")
        @Size(max = 255, message = "Phone must not exceed 255 characters")
        String phone,

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255, message = "Address line 1 must not exceed 255 characters")
        String addressLine1,

        @Size(max = 255, message = "Address line 2 must not exceed 255 characters")
        String addressLine2,

        @Size(max = 255, message = "City must not exceed 255 characters")
        String city,

        @Size(max = 255, message = "Zip code must not exceed 255 characters")
        String zipCode,

        @NotNull(message = "Country ID is required")
        Long countryId,

        Long stateOrProvinceId,

        Long districtId
) {
}
