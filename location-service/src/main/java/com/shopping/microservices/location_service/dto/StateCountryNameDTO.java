package com.shopping.microservices.location_service.dto;

public record StateCountryNameDTO(
        Long stateOrProvinceId,
        String stateOrProvinceName,
        String countryName
) {
}
