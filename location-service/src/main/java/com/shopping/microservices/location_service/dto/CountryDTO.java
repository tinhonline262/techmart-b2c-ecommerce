package com.shopping.microservices.location_service.dto;

import java.time.LocalDateTime;

public record CountryDTO(
        Long id,
        String name,
        String code2,
        String code3,
        Boolean isBillingEnabled,
        Boolean isShippingEnabled,
        Boolean isCityEnabled,
        Boolean isZipCodeEnabled,
        Boolean isDistrictEnabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
