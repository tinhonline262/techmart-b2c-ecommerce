package com.shopping.microservices.location_service.dto;

import jakarta.validation.constraints.Size;

public record DistrictUpdateDTO(
        @Size(max = 450, message = "District name must not exceed 450 characters")
        String name,

        @Size(max = 450, message = "Type must not exceed 450 characters")
        String type,

        @Size(max = 255, message = "Location must not exceed 255 characters")
        String location,

        Long stateOrProvinceId
) {
}
