package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.CustomerAdminDTO;
import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerListDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
import com.shopping.microservices.customer_service.service.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerListDTO>>> getCustomers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<CustomerListDTO> customers = customerService.getAllCustomers(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Customers retrieved successfully", customers, request.getRequestURI())
        );
    }

    @GetMapping(params = "email")
    public ResponseEntity<ApiResponse<CustomerAdminDTO>> getCustomerByEmail(
            @RequestParam String email,
            HttpServletRequest request) {

        // TODO: Implement service call to fetch customer by email
        // CustomerAdminDTO customer = customerService.getCustomerByEmail(email);

        return ResponseEntity.ok(
                ApiResponse.success("Customer retrieved successfully", null, request.getRequestURI())
        );
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerProfile(
            @PathVariable Long id,
            HttpServletRequest request) {

        CustomerDTO customer = customerService.getCustomerById(id);

        return ResponseEntity.ok(
                ApiResponse.success("Customer profile retrieved successfully", customer, request.getRequestURI())
        );
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<Void> updateCustomerProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerPostDTO customerDTO,
            HttpServletRequest request) {

        customerService.updateCustomer(id, customerDTO);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Void> deleteCustomerProfile(@PathVariable Long id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(
            @Valid @RequestBody CustomerPostDTO customerDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {

        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);

        URI location = uriBuilder
                .path("/api/v1/customers/profile/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(ApiResponse.success("Customer created successfully", createdCustomer, request.getRequestURI()));
    }
}