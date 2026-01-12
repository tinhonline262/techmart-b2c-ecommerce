package com.shopping.microservices.location_service.dto;

import java.time.LocalDateTime;

public record AddressDTO(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        Long countryId,
        Long stateOrProvinceId,
        Long districtId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
