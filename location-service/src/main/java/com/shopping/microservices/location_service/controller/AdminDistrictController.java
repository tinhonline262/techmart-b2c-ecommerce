package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/districts")
@RequiredArgsConstructor
public class AdminDistrictController {


    @GetMapping("/{stateOrProvinceId}")
    public ResponseEntity<ApiResponse<List<DistrictListGetDTO>>> getDistrictsByStateOrProvinceId(
            @PathVariable Long stateOrProvinceId) {
        // TODO: Implement get districts by state/province id
        return ResponseEntity.ok(ApiResponse.success("Districts retrieved successfully", null, "/api/v1/districts/" + stateOrProvinceId));
    }
}
