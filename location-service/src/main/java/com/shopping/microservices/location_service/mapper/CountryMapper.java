package com.shopping.microservices.location_service.mapper;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import com.shopping.microservices.location_service.entity.Country;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class CountryMapper {
    
    /**
     * Convert Country entity to CountryDTO
     */
    public CountryDTO toDTO(Country country) {
        if (country == null) {
            return null;
        }
        
        return new CountryDTO(
                country.getId(),
                country.getName(),
                country.getCode2(),
                country.getCode3(),
                country.getIsBillingEnabled(),
                country.getIsShippingEnabled(),
                country.getIsCityEnabled(),
                country.getIsZipCodeEnabled(),
                country.getIsDistrictEnabled(),
                country.getCreatedAt() != null ? country.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                country.getUpdatedAt() != null ? country.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
    }
    
    /**
     * Convert Country entity to CountryListGetDTO
     */
    public CountryListGetDTO toListGetDTO(Country country) {
        if (country == null) {
            return null;
        }
        
        return new CountryListGetDTO(
                country.getId(),
                country.getName(),
                country.getCode2(),
                country.getCode3()
        );
    }
    
    /**
     * Convert CountryPostDTO to Country entity (for creation)
     */
    public Country toEntity(CountryPostDTO countryPostDTO) {
        if (countryPostDTO == null) {
            return null;
        }
        
        return Country.builder()
                .name(countryPostDTO.name())
                .code2(countryPostDTO.code2())
                .code3(countryPostDTO.code3())
                .isBillingEnabled(countryPostDTO.isBillingEnabled())
                .isShippingEnabled(countryPostDTO.isShippingEnabled())
                .isCityEnabled(countryPostDTO.isCityEnabled())
                .isZipCodeEnabled(countryPostDTO.isZipCodeEnabled())
                .isDistrictEnabled(countryPostDTO.isDistrictEnabled())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
    
    /**
     * Update Country entity from CountryPostDTO
     */
    public void updateEntity(Country country, CountryPostDTO countryPostDTO) {
        if (country == null || countryPostDTO == null) {
            return;
        }
        
        country.setName(countryPostDTO.name());
        country.setCode2(countryPostDTO.code2());
        country.setCode3(countryPostDTO.code3());
        country.setIsBillingEnabled(countryPostDTO.isBillingEnabled());
        country.setIsShippingEnabled(countryPostDTO.isShippingEnabled());
        country.setIsCityEnabled(countryPostDTO.isCityEnabled());
        country.setIsZipCodeEnabled(countryPostDTO.isZipCodeEnabled());
        country.setIsDistrictEnabled(countryPostDTO.isDistrictEnabled());
        country.setUpdatedAt(Instant.now());
    }
}
