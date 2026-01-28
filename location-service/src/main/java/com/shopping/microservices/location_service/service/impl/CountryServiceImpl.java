package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.service.CountryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    @Transactional
    public CountryDTO createCountry(CountryPostDTO countryPostDTO) {
        log.info("Creating country: {}", countryPostDTO.name());

        Country country = Country.builder()
                .name(countryPostDTO.name())
                .code2(countryPostDTO.code2())
                .code3(countryPostDTO.code3())
                .isBillingEnabled(countryPostDTO.isBillingEnabled() != null ? countryPostDTO.isBillingEnabled() : true)
                .isShippingEnabled(countryPostDTO.isShippingEnabled() != null ? countryPostDTO.isShippingEnabled() : true)
                .isCityEnabled(countryPostDTO.isCityEnabled() != null ? countryPostDTO.isCityEnabled() : true)
                .isZipCodeEnabled(countryPostDTO.isZipCodeEnabled() != null ? countryPostDTO.isZipCodeEnabled() : true)
                .isDistrictEnabled(countryPostDTO.isDistrictEnabled() != null ? countryPostDTO.isDistrictEnabled() : true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Country savedCountry = countryRepository.save(country);
        log.info("Country created successfully with id: {}", savedCountry.getId());

        return mapToDTO(savedCountry);
    }

    @Override
    @Transactional
    public CountryDTO updateCountry(Long id, CountryPostDTO countryPostDTO) {
        log.info("Updating country id: {}", id);

        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));

        country.setName(countryPostDTO.name());
        country.setCode2(countryPostDTO.code2());
        country.setCode3(countryPostDTO.code3());
        country.setIsBillingEnabled(countryPostDTO.isBillingEnabled() != null ? countryPostDTO.isBillingEnabled() : true);
        country.setIsShippingEnabled(countryPostDTO.isShippingEnabled() != null ? countryPostDTO.isShippingEnabled() : true);
        country.setIsCityEnabled(countryPostDTO.isCityEnabled() != null ? countryPostDTO.isCityEnabled() : true);
        country.setIsZipCodeEnabled(countryPostDTO.isZipCodeEnabled() != null ? countryPostDTO.isZipCodeEnabled() : true);
        country.setIsDistrictEnabled(countryPostDTO.isDistrictEnabled() != null ? countryPostDTO.isDistrictEnabled() : true);
        country.setUpdatedAt(Instant.now());

        Country updatedCountry = countryRepository.save(country);
        log.info("Country updated successfully");

        return mapToDTO(updatedCountry);
    }

    @Override
    @Transactional
    public void deleteCountry(Long id) {
        log.info("Deleting country id: {}", id);

        if (!countryRepository.existsById(id)) {
            throw new RuntimeException("Country not found with id: " + id);
        }

        countryRepository.deleteById(id);
        log.info("Country deleted successfully");
    }

    private CountryListGetDTO mapToListDTO(Country country) {
        return new CountryListGetDTO(
                country.getId(),
                country.getName(),
                country.getCode2(),
                country.getCode3()
        );
    }

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