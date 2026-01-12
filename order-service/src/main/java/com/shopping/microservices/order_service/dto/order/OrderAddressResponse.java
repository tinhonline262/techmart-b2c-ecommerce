package com.shopping.microservices.order_service.dto.order;

import lombok.Builder;

@Builder
public record OrderAddressResponse(
        Long id,
        String contactName,
        String phone,
        String addressLine1,
        String addressLine2,
        String city,
        String zipCode,
        Long districtId,
        String districtName,
        Long stateOrProvinceId,
        String stateOrProvinceName,
        Long countryId,
        String countryName
) {
}
