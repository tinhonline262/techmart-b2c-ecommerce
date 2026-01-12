package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.repository.DistrictRepository;
import com.shopping.microservices.location_service.repository.StateOrProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PublicLocationController {

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<CountryListGetDTO>>> getCountries() {
        // TODO: Implement get all countries for public
        return ResponseEntity.ok(ApiResponse.success("Countries retrieved successfully", null, "/api/v1/public/countries"));
    }

    @GetMapping("/state-or-provinces/{countryId}")
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvincesByCountryId(
            @PathVariable Long countryId) {
        // TODO: Implement get states/provinces by country id
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", null, "/api/v1/public/state-or-provinces/" + countryId));
    }

    @GetMapping("/districts/{stateOrProvinceId}")
    public ResponseEntity<ApiResponse<List<DistrictListGetDTO>>> getDistrictsByStateOrProvinceId(
            @PathVariable Long stateOrProvinceId) {
        // TODO: Implement get districts by state/province id
        return ResponseEntity.ok(ApiResponse.success("Districts retrieved successfully", null, "/api/v1/public/districts/" + stateOrProvinceId));
    }
}
