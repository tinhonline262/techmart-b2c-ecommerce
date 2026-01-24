package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import com.shopping.microservices.location_service.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        Page<CountryDTO> page = countryService.getCountriesPaging(pageable);
        
        return ResponseEntity.ok(ApiResponse.success("Countries retrieved successfully", page, "/api/v1/countries/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CountryListGetDTO>>> getCountries() {
        List<CountryListGetDTO> countries = countryService.getAllCountries();
        return ResponseEntity.ok(ApiResponse.success("Countries retrieved successfully", countries, "/api/v1/countries"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryDTO>> getCountryById(@PathVariable Long id) {
        CountryDTO countryDTO = countryService.getCountryById(id);
        return ResponseEntity.ok(ApiResponse.success("Country retrieved successfully", countryDTO, "/api/v1/countries/" + id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CountryDTO>> createCountry(@Valid @RequestBody CountryPostDTO countryPostDTO) {
        CountryDTO countryDTO = countryService.createCountry(countryPostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Country created successfully", countryDTO, "/api/v1/countries"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CountryDTO>> updateCountry(
            @PathVariable Long id,
            @Valid @RequestBody CountryPostDTO countryPostDTO) {
        CountryDTO countryDTO = countryService.updateCountry(id, countryPostDTO);
        return ResponseEntity.ok(ApiResponse.success("Country updated successfully", countryDTO, "/api/v1/countries/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
        return ResponseEntity.ok(ApiResponse.success("Country deleted successfully", null, "/api/v1/countries/" + id));
    }
}

            @Valid @RequestBody CountryPostDTO countryPostDTO) {
        
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        
        country.setName(countryPostDTO.name());
        country.setCode2(countryPostDTO.code2());
        country.setCode3(countryPostDTO.code3());
        country.setIsBillingEnabled(countryPostDTO.isBillingEnabled());
        country.setIsShippingEnabled(countryPostDTO.isShippingEnabled());
        country.setIsCityEnabled(countryPostDTO.isCityEnabled());
        country.setIsZipCodeEnabled(countryPostDTO.isZipCodeEnabled());
        country.setIsDistrictEnabled(countryPostDTO.isDistrictEnabled());
        country.setUpdatedAt(Instant.now());
        
        Country updatedCountry = countryRepository.save(country);
        
        CountryDTO countryDTO = new CountryDTO(
                updatedCountry.getId(),
                updatedCountry.getName(),
                updatedCountry.getCode2(),
                updatedCountry.getCode3(),
                updatedCountry.getIsBillingEnabled(),
                updatedCountry.getIsShippingEnabled(),
                updatedCountry.getIsCityEnabled(),
                updatedCountry.getIsZipCodeEnabled(),
                updatedCountry.getIsDistrictEnabled(),
                updatedCountry.getCreatedAt() != null ? updatedCountry.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                updatedCountry.getUpdatedAt() != null ? updatedCountry.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
        
        return ResponseEntity.ok(ApiResponse.success("Country updated successfully", countryDTO, "/api/v1/countries/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCountry(@PathVariable Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        
        countryRepository.delete(country);
        
        return ResponseEntity.ok(ApiResponse.success("Country deleted successfully", null, "/api/v1/countries/" + id));
    }
}
