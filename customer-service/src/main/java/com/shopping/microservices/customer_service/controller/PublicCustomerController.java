package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Public/Customer Controller for Customer Profile Management
 * Base path: /api/v1/public/customer
 */
@RestController
@RequestMapping("/api/v1/public/customer")
@RequiredArgsConstructor
public class PublicCustomerController {
    

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerProfile(HttpServletRequest request) {
        
        // TODO: Extract authenticated user from SecurityContext
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String userId = authentication.getName();
        
        // TODO: Implement service call to fetch customer profile
        // CustomerDTO customer = customerService.getCustomerProfile(userId);
        
        return ResponseEntity.ok(
                ApiResponse.success("Customer profile retrieved successfully", null, request.getRequestURI())
        );
    }

    @PostMapping("/guest-user")
    public ResponseEntity<ApiResponse<CustomerDTO>> createGuestUser(
            @Valid @RequestBody CustomerPostDTO customerDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {
        
        // TODO: Implement service call to create guest user
        // CustomerDTO createdCustomer = customerService.createGuestUser(customerDTO);
        
        // Build location URI for created resource
        URI location = uriBuilder
                .path("/api/v1/public/customer/profile")
                .build()
                .toUri();
        
        return ResponseEntity.created(location)
                .body(ApiResponse.success("Guest user created successfully",null,  request.getRequestURI()));
    }
}
