package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import com.shopping.microservices.location_service.entity.StateOrProvince;
import com.shopping.microservices.location_service.service.StateOrProvinceService;
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
@RequestMapping("/api/v1/state-or-provinces")
@RequiredArgsConstructor
public class AdminStateOrProvinceController {

    private final StateOrProvinceService stateOrProvinceService;

    @GetMapping("/paging")
    public ResponseEntity<ApiResponse<Object>> getStateOrProvincesPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long countryId) {
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<StateOrProvinceDTO> page = stateOrProvinceService.getStateOrProvincesPaging(pageable, countryId);
        
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", page, "/api/v1/state-or-provinces/paging"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StateOrProvinceListGetDTO>>> getStateOrProvinces(
            @RequestParam(required = false) Long countryId) {
        
        List<StateOrProvinceListGetDTO> stateOrProvinces = stateOrProvinceService.getAllStateOrProvinces(countryId);
        return ResponseEntity.ok(ApiResponse.success("States/Provinces retrieved successfully", stateOrProvinces, "/api/v1/state-or-provinces"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> getStateOrProvinceById(@PathVariable Long id) {
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.getStateOrProvinceById(id);
        return ResponseEntity.ok(ApiResponse.success("State/Province retrieved successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @GetMapping("/state-country-names")
    public ResponseEntity<ApiResponse<List<StateCountryNameDTO>>> getStateCountryNames(
            @RequestParam List<Long> stateOrProvinceIds) {
        
        List<StateCountryNameDTO> result = stateOrProvinceService.getStateCountryNames(stateOrProvinceIds);
        return ResponseEntity.ok(ApiResponse.success("State/Country names retrieved successfully", result, "/api/v1/state-or-provinces/state-country-names"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> createStateOrProvince(
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.createStateOrProvince(stateOrProvincePostDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("State/Province created successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StateOrProvinceDTO>> updateStateOrProvince(
            @PathVariable Long id,
            @Valid @RequestBody StateOrProvincePostDTO stateOrProvincePostDTO) {
        
        StateOrProvinceDTO stateOrProvinceDTO = stateOrProvinceService.updateStateOrProvince(id, stateOrProvincePostDTO);
        return ResponseEntity.ok(ApiResponse.success("State/Province updated successfully", stateOrProvinceDTO, "/api/v1/state-or-provinces/" + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStateOrProvince(@PathVariable Long id) {
        stateOrProvinceService.deleteStateOrProvince(id);
        return ResponseEntity.ok(ApiResponse.success("State/Province deleted successfully", null, "/api/v1/state-or-provinces/" + id));
    }
}
