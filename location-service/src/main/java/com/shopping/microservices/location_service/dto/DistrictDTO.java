package com.shopping.microservices.location_service.dto;

import java.time.LocalDateTime;

public record DistrictDTO(
        Long id,
        String name,
        String type,
        String location,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
