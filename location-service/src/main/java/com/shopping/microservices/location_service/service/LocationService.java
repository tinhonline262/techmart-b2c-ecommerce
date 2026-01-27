package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;

import java.util.List;

/**
 * Service interface for Location operations
 */
public interface LocationService {

    /**
     * Get all countries
     * @return list of countries
     */
    List<CountryListGetDTO> getAllCountries();

    /**
     * Get states/provinces by country ID
     * @param countryId country ID
     * @return list of states/provinces
     */
    List<StateOrProvinceListGetDTO> getStatesByCountryId(Long countryId);

    /**
     * Get districts by state/province ID
     * @param stateOrProvinceId state/province ID
     * @return list of districts
     */
    List<DistrictListGetDTO> getDistrictsByStateOrProvinceId(Long stateOrProvinceId);
}