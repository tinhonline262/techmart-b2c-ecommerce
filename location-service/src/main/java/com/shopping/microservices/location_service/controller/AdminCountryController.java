package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import com.shopping.microservices.location_service.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class AdminCountryController {

    private final CountryService countryService;

    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Object>> getCountriesPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        // TODO: Implement pagination logic
        return ResponseEntity.ok(ApiResponse.success("Countries retrieved successfully", null, "/api/v1/countries/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryListGetDTO>>> getCountries() {
        List<CountryListGetDTO> countries = countryService.getAllCountries();
        return ResponseEntity.ok(ApiResponse.success("Countries retrieved successfully", countries, "/api/v1/countries"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryDTO>> getCountryById(@PathVariable Long id) {
        CountryDTO country = countryService.getCountryById(id);
        return ResponseEntity.ok(ApiResponse.success("Country retrieved successfully", country, "/api/v1/countries/" + id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CountryDTO>> createCountry(@Valid @RequestBody CountryPostDTO countryPostDTO) {
        CountryDTO country = countryService.createCountry(countryPostDTO);
        return ResponseEntity.ok(ApiResponse.success("Country created successfully", country, "/api/v1/countries"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryDTO>> updateCountry(
            @PathVariable Long id,
            @Valid @RequestBody CountryPostDTO countryPostDTO) {
        CountryDTO country = countryService.updateCountry(id, countryPostDTO);
        return ResponseEntity.ok(ApiResponse.success("Country updated successfully", country, "/api/v1/countries/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.ok(ApiResponse.success("Country deleted successfully", null, "/api/v1/countries/" + id));
    }
}