package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicLocationController {

    private final LocationService locationService;

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<CountryListGetDTO>>> getCountries() {
        List<CountryListGetDTO> countries = locationService.getAllCountries();
        return ResponseEntity.ok(
                ApiResponse.success("Countries retrieved successfully", countries, "/api/v1/public/countries")
        );
    }

    @GetMapping("/state-or-provinces/{countryId}")
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvincesByCountryId(
            @PathVariable Long countryId) {
        List<StateOrProvinceListGetDTO> states = locationService.getStatesByCountryId(countryId);
        return ResponseEntity.ok(
                ApiResponse.success("States/Provinces retrieved successfully", states, "/api/v1/public/state-or-provinces/" + countryId)
        );
    }

    @GetMapping("/districts/{stateOrProvinceId}")
    public ResponseEntity<ApiResponse<List<DistrictListGetDTO>>> getDistrictsByStateOrProvinceId(
            @PathVariable Long stateOrProvinceId) {
        List<DistrictListGetDTO> districts = locationService.getDistrictsByStateOrProvinceId(stateOrProvinceId);
        return ResponseEntity.ok(
                ApiResponse.success("Districts retrieved successfully", districts, "/api/v1/public/districts/" + stateOrProvinceId)
        );
    }
}