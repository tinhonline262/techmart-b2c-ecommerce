package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.entity.District;
import com.shopping.microservices.location_service.entity.StateOrProvince;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.repository.DistrictRepository;
import com.shopping.microservices.location_service.repository.StateOrProvinceRepository;
import com.shopping.microservices.location_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of LocationService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final CountryRepository countryRepository;
    private final StateOrProvinceRepository stateOrProvinceRepository;
    private final DistrictRepository districtRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CountryListGetDTO> getAllCountries() {
        log.info("Fetching all countries");

        List<Country> countries = countryRepository.findAll();

        return countries.stream()
                .map(this::mapToCountryListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StateOrProvinceListGetDTO> getStatesByCountryId(Long countryId) {
        log.info("Fetching states/provinces for country ID: {}", countryId);

        List<StateOrProvince> states = stateOrProvinceRepository.findByCountryId(countryId);

        return states.stream()
                .map(this::mapToStateListDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictListGetDTO> getDistrictsByStateOrProvinceId(Long stateOrProvinceId) {
        log.info("Fetching districts for state/province ID: {}", stateOrProvinceId);

        List<District> districts = districtRepository.findByStateOrProvinceId(stateOrProvinceId);

        return districts.stream()
                .map(this::mapToDistrictListDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map Country entity to CountryListGetDTO
     */
    private CountryListGetDTO mapToCountryListDTO(Country country) {
        return new CountryListGetDTO(
                country.getId(),
                country.getName(),
                country.getCode2(),
                country.getCode3()
        );
    }

    /**
     * Map StateOrProvince entity to StateOrProvinceListGetDTO
     */
    private StateOrProvinceListGetDTO mapToStateListDTO(StateOrProvince state) {
        return new StateOrProvinceListGetDTO(
                state.getId(),
                state.getCode(),
                state.getName(),
                state.getType(),
                state.getCountry().getId()  // ← SỬA: Lấy ID từ Country object
        );
    }

    /**
     * Map District entity to DistrictListGetDTO
     */
    private DistrictListGetDTO mapToDistrictListDTO(District district) {
        return new DistrictListGetDTO(
                district.getId(),
                district.getName(),
                district.getType(),
                district.getLocation()
        );
    }
}