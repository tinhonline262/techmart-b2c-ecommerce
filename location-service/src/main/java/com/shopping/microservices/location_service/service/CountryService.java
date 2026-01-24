package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CountryService {
    
    /**
     * Get all countries with pagination
     */
    Page<CountryDTO> getCountriesPaging(Pageable pageable);
    
    /**
     * Get all countries as a simple list
     */
    List<CountryListGetDTO> getAllCountries();
    
    /**
     * Get country by ID
     */
    CountryDTO getCountryById(Long id);
    
    /**
     * Create a new country
     */
    CountryDTO createCountry(CountryPostDTO countryPostDTO);
    
    /**
     * Update an existing country
     */
    CountryDTO updateCountry(Long id, CountryPostDTO countryPostDTO);
    
    /**
     * Delete a country
     */
    void deleteCountry(Long id);
}
