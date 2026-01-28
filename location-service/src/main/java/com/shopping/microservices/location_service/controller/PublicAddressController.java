package com.shopping.microservices.location_service.controller;

import com.shopping.microservices.location_service.dto.AddressDTO;
import com.shopping.microservices.location_service.dto.AddressPostDTO;
import com.shopping.microservices.location_service.dto.ApiResponse;
import com.shopping.microservices.location_service.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/addresses")
@RequiredArgsConstructor
public class PublicAddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(@Valid @RequestBody AddressPostDTO addressPostDTO) {
        AddressDTO address = addressService.createAddress(addressPostDTO);
        return ResponseEntity.ok(ApiResponse.success("Address created successfully", address, "/api/v1/public/addresses"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressPostDTO addressPostDTO) {
        AddressDTO address = addressService.updateAddress(id, addressPostDTO);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address, "/api/v1/public/addresses/" + id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(@PathVariable Long id) {
        AddressDTO address = addressService.getAddressById(id);
        return ResponseEntity.ok(ApiResponse.success("Address retrieved successfully", address, "/api/v1/public/addresses/" + id));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddressesByIds(@RequestParam List<Long> ids) {
        List<AddressDTO> addresses = addressService.getAddressesByIds(ids);
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses, "/api/v1/public/addresses"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null, "/api/v1/public/addresses/" + id));
    }
}