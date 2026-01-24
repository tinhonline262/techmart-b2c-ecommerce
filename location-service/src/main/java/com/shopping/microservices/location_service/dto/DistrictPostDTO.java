package com.shopping.microservices.location_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DistrictPostDTO(
        @NotBlank(message = "District name is required")
        @Size(max = 450, message = "District name must not exceed 450 characters")
        String name,

        @Size(max = 450, message = "Type must not exceed 450 characters")
        String type,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        @NotNull(message = "State/Province ID is required")
        Long stateOrProvinceId
) {
}
