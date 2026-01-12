package com.shopping.microservices.location_service.dto;

public record CountryListGetDTO(
        Long id,
        String name,
        String code2,
        String code3
) {
}
