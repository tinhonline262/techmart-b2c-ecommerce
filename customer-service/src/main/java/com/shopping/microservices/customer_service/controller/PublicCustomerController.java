package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
import com.shopping.microservices.customer_service.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/public/customer")
@RequiredArgsConstructor
public class PublicCustomerController {

    private final CustomerService customerService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Public endpoint works!");
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerProfile(
            @RequestParam(required = false) String username,
            HttpServletRequest request) {

        // Nếu có username trong param thì dùng, không thì dùng từ Security
        String user = username;
        if (user == null || user.isEmpty()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user = authentication.getName();
        }

        CustomerDTO customer = customerService.getCustomerProfile(user);

        return ResponseEntity.ok(
                ApiResponse.success("Customer profile retrieved successfully", customer, request.getRequestURI())
        );
    }

    @PostMapping("/guest-user")
    public ResponseEntity<ApiResponse<CustomerDTO>> createGuestUser(
            @Valid @RequestBody CustomerPostDTO customerDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {

        CustomerDTO createdCustomer = customerService.createGuestUser(customerDTO);

        URI location = uriBuilder
                .path("/api/v1/public/customer/profile")
                .build()
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success("Guest user created successfully", createdCustomer, request.getRequestURI()));
    }
}