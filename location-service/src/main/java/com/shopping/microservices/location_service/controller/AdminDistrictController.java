package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class AdminDistrictController {

    private final DistrictService districtService;

    @GetMapping("/{stateOrProvinceId}")
    public ResponseEntity<ApiResponse<List<DistrictListGetDTO>>> getDistrictsByStateOrProvinceId(
            @PathVariable Long stateOrProvinceId) {
        
        List<DistrictListGetDTO> districts = districtService.getDistrictsByStateOrProvinceId(stateOrProvinceId);
        return ResponseEntity.ok(ApiResponse.success("Districts retrieved successfully", districts, "/api/v1/districts/" + stateOrProvinceId));
    }
}
