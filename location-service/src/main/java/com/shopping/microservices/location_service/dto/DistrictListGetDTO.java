package com.shopping.microservices.location_service.dto;

public record DistrictListGetDTO(
        Long id,
        String name,
        String type,
        String location
) {
}
