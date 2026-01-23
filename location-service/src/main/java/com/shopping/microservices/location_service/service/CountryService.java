package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;

import java.util.List;

/**
 * Service interface for Country operations
 */
public interface CountryService {

    /**
     * Get all countries
     * @return list of countries
     */
    List<CountryListGetDTO> getAllCountries();

    /**
     * Get country by ID
     * @param id country ID
     * @return country DTO
     */
    CountryDTO getCountryById(Long id);
}