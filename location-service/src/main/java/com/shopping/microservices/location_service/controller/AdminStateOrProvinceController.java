package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import com.shopping.microservices.location_service.repository.StateOrProvinceRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/state-or-provinces")
@RequiredArgsConstructor
public class AdminStateOrProvinceController {


    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Object>> getStateOrProvincesPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long countryId) {
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        // TODO: Implement pagination logic with optional country filter
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", null, "/api/v1/state-or-provinces/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvinces(
            @RequestParam(required = false) Long countryId) {
        // TODO: Implement get all states/provinces with optional country filter
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", null, "/api/v1/state-or-provinces"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> getStateOrProvinceById(@PathVariable Long id) {
        // TODO: Implement get state/province by id
        return ResponseEntity.ok(ApiResponse.success("State/Province retrieved successfully", null, "/api/v1/state-or-provinces/" + id));
    }

    @GetMapping("/state-country-names")
    public ResponseEntity<ApiResponse<List<StateCountryNameDTO>>> getStateCountryNames(
            @RequestParam List<Long> stateOrProvinceIds) {
        // TODO: Implement batch get state and country names
        return ResponseEntity.ok(ApiResponse.success("State/Country names retrieved successfully", null, "/api/v1/state-or-provinces/state-country-names"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> createStateOrProvince(
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        // TODO: Implement create state/province
        return ResponseEntity.ok(ApiResponse.success("State/Province created successfully", null, "/api/v1/state-or-provinces"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> updateStateOrProvince(
            @PathVariable Long id,
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        // TODO: Implement update state/province
        return ResponseEntity.ok(ApiResponse.success("State/Province updated successfully", null, "/api/v1/state-or-provinces/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStateOrProvince(@PathVariable Long id) {
        // TODO: Implement delete state/province
        return ResponseEntity.ok(ApiResponse.success("State/Province deleted successfully", null, "/api/v1/state-or-provinces/" + id));
    }
}
