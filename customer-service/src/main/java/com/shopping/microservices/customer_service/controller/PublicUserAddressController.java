package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.UserAddressDTO;
import com.shopping.microservices.customer_service.dto.UserAddressPostDTO;
import com.shopping.microservices.customer_service.service.UserAddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/customer/addresses")
@RequiredArgsConstructor
public class PublicUserAddressController {

    private final UserAddressService userAddressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressDTO>>> getUserAddresses(
            @RequestParam(required = false) String userId,
            HttpServletRequest request) {

        // Get userId from param or authentication
        String user = userId;
        if (user == null || user.isEmpty()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = authentication.getName();
        }

        List<UserAddressDTO> addresses = userAddressService.getUserAddresses(user);

        return ResponseEntity.ok(
                ApiResponse.success("User addresses retrieved successfully", addresses, request.getRequestURI())
        );
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<UserAddressDTO>> getDefaultAddress(
            @RequestParam(required = false) String userId,
            HttpServletRequest request) {

        // Get userId from param or authentication
        String user = userId;
        if (user == null || user.isEmpty()) {
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        UserAddressDTO defaultAddress = userAddressService.getDefaultAddress(user);

        return ResponseEntity.ok(
                ApiResponse.success("Default address retrieved successfully", defaultAddress, request.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressDTO>> createUserAddress(
            @RequestParam(required = false) String userId,
            @Valid @RequestBody UserAddressPostDTO addressDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {

        // Get userId from param or authentication
        String user = userId;
        if (user == null || user.isEmpty()) {
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        UserAddressDTO createdAddress = userAddressService.createUserAddress(user, addressDTO);

        URI location = uriBuilder
                .path("/api/v1/public/customer/addresses/{id}")
                .buildAndExpand(createdAddress.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success("User address created successfully", createdAddress, request.getRequestURI()));
    }

    @PutMapping("/{id}/set-default")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @PathVariable Long id,
            @RequestParam(required = false) String userId,
            HttpServletRequest request) {

        // Get userId from param or authentication
        String user = userId;
        if (user == null || user.isEmpty()) {
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        userAddressService.setDefaultAddress(user, id);

        return ResponseEntity.ok(
                ApiResponse.success("Default address set successfully", null, request.getRequestURI())
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserAddress(
            @PathVariable Long id,
            @RequestParam(required = false) String userId,
            HttpServletRequest request) {

        // Get userId from param or authentication
        String user = userId;
        if (user == null || user.isEmpty()) {
            user = SecurityContextHolder.getContext().getAuthentication().getName();
        }

        userAddressService.deleteUserAddress(user, id);

        return ResponseEntity.ok(
                ApiResponse.success("User address deleted successfully", null, request.getRequestURI())
        );
    }
}