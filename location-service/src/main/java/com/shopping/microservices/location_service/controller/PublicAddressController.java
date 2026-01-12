package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.AddressDTO;
import com.shopping.microservices.location_service.dto.AddressPostDTO;
import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.repository.AddressRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/addresses")
@RequiredArgsConstructor
public class PublicAddressController {

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(@Valid @RequestBody AddressPostDTO addressPostDTO) {
        // TODO: Implement create address
        return ResponseEntity.ok(ApiResponse.success("Address created successfully", null, "/api/v1/public/addresses"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressPostDTO addressPostDTO) {
        // TODO: Implement update address
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", null, "/api/v1/public/addresses/" + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(@PathVariable Long id) {
        // TODO: Implement get address by id
        return ResponseEntity.ok(ApiResponse.success("Address retrieved successfully", null, "/api/v1/public/addresses/" + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddressesByIds(@RequestParam List<Long> ids) {
        // TODO: Implement get addresses by multiple ids
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", null, "/api/v1/public/addresses"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        // TODO: Implement delete address
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null, "/api/v1/public/addresses/" + id));
    }
}
