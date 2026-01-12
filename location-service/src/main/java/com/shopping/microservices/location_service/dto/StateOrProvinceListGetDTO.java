package com.shopping.microservices.location_service.dto;

public record StateOrProvinceListGetDTO(
        Long id,
        String code,
        String name,
        String type,
        Long countryId
) {
}
