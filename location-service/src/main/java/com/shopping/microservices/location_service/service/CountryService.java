package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;

import java.util.List;

public interface CountryService {
    List<CountryListGetDTO> getAllCountries();
    CountryDTO getCountryById(Long id);
    CountryDTO createCountry(CountryPostDTO countryPostDTO);
    CountryDTO updateCountry(Long id, CountryPostDTO countryPostDTO);
    void deleteCountry(Long id);
}