package com.shopping.microservices.location_service.dto;

import java.time.LocalDateTime;

public record StateOrProvinceDTO(
        Long id,
        String code,
        String name,
        String type,
        Long countryId,
        String countryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
