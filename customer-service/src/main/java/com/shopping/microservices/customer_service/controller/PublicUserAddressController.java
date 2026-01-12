package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.UserAddressDTO;
import com.shopping.microservices.customer_service.dto.UserAddressPostDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("/api/v1/public/customer/addresses")
@RequiredArgsConstructor
public class PublicUserAddressController {
    

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAddressDTO>>> getUserAddresses(HttpServletRequest request) {
        
        // TODO: Extract authenticated user from SecurityContext
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String userId = authentication.getName();
        
        // TODO: Implement service call to fetch user addresses
        // List<UserAddressDTO> addresses = userAddressService.getUserAddresses(userId);
        
        return ResponseEntity.ok(
                ApiResponse.success( "User addresses retrieved successfully",null, request.getRequestURI())
        );
    }

    @GetMapping("/default")
    public ResponseEntity<ApiResponse<UserAddressDTO>> getDefaultAddress(HttpServletRequest request) {
        
        // TODO: Extract authenticated user from SecurityContext
        // String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // TODO: Implement service call to fetch default address
        // UserAddressDTO defaultAddress = userAddressService.getDefaultAddress(userId);
        
        return ResponseEntity.ok(
                ApiResponse.success( "Default address retrieved successfully",null, request.getRequestURI())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserAddressDTO>> createUserAddress(
            @Valid @RequestBody UserAddressPostDTO addressDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {
        
        // TODO: Extract authenticated user from SecurityContext
        // String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // TODO: Implement service call to create user address
        // UserAddressDTO createdAddress = userAddressService.createUserAddress(userId, addressDTO);
        
        // Build location URI for created resource
        URI location = uriBuilder
                .path("/api/v1/public/customer/addresses/{id}")
                .buildAndExpand(1L) // TODO: Use actual created address ID
                .toUri();
        
        return ResponseEntity.created(location)
                .body(ApiResponse.success( "User address created successfully",null, request.getRequestURI()));
    }
    

    @PutMapping("/{id}/set-default")
    public ResponseEntity<Void> setDefaultAddress(@PathVariable Long id) {
        
        // TODO: Extract authenticated user from SecurityContext
        // String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // TODO: Implement service call to set default address
        // userAddressService.setDefaultAddress(userId, id);
        
        return ResponseEntity.noContent().build();
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserAddress(@PathVariable Long id) {
        
        // TODO: Extract authenticated user from SecurityContext
        // String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // TODO: Implement service call to delete user address
        // userAddressService.deleteUserAddress(userId, id);
        
        return ResponseEntity.noContent().build();
    }
}
