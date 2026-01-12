package com.shopping.microservices.location_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StateOrProvincePostDTO(
        @Size(max = 255, message = "Code must not exceed 255 characters")
        String code,

        @NotBlank(message = "State/Province name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,

        @Size(max = 255, message = "Type must not exceed 255 characters")
        String type,

        @NotNull(message = "Country ID is required")
        Long countryId
) {
}
