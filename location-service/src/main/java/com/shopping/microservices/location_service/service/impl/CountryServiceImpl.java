package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of CountryService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CountryListGetDTO> getAllCountries() {
        log.info("Fetching all countries");

        List<Country> countries = countryRepository.findAll();

        return countries.stream()
                .map(this::mapToListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CountryDTO getCountryById(Long id) {
        log.info("Fetching country by id: {}", id);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        return mapToDTO(country);
    }

    /**
     * Map Country entity to CountryListGetDTO
     */
    private CountryListGetDTO mapToListDTO(Country country) {
        return new CountryListGetDTO(
                country.getId(),
                country.getName(),
                country.getCode2(),
                country.getCode3()
        );
    }

    /**
     * Map Country entity to CountryDTO
     */
    private CountryDTO mapToDTO(Country country) {
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
                country.getCreatedAt() != null ?
                        LocalDateTime.ofInstant(country.getCreatedAt(), ZoneId.systemDefault()) : null,
                country.getUpdatedAt() != null ?
                        LocalDateTime.ofInstant(country.getUpdatedAt(), ZoneId.systemDefault()) : null
        );
    }
}