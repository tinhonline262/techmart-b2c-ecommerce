package com.shopping.microservices.customer_service.controller;

import com.shopping.microservices.customer_service.dto.ApiResponse;
import com.shopping.microservices.customer_service.dto.CustomerAdminDTO;
import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerListDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
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

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerListDTO>>> getCustomers(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            HttpServletRequest request) {
        
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        
        // TODO: Implement service call to fetch customers
        // Page<CustomerListDTO> customers = customerService.getAllCustomers(pageable);
        
        return ResponseEntity.ok(
                ApiResponse.success( "Customers retrieved successfully", null, request.getRequestURI())
        );
    }

    @GetMapping(params = "email")
    public ResponseEntity<ApiResponse<CustomerAdminDTO>> getCustomerByEmail(
            @RequestParam String email,
            HttpServletRequest request) {
        
        // TODO: Implement service call to fetch customer by email
        // CustomerAdminDTO customer = customerService.getCustomerByEmail(email);
        
        return ResponseEntity.ok(
                ApiResponse.success("Customer retrieved successfully",null,  request.getRequestURI())
        );
    }
    

    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getCustomerProfile(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        // TODO: Implement service call to fetch customer profile
        // CustomerDTO customer = customerService.getCustomerById(id);
        
        return ResponseEntity.ok(
                ApiResponse.success("Customer profile retrieved successfully",null,  request.getRequestURI())
        );
    }
    

    @PutMapping("/profile/{id}")
    public ResponseEntity<Void> updateCustomerProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerPostDTO customerDTO,
            HttpServletRequest request) {
        
        // TODO: Implement service call to update customer
        // customerService.updateCustomer(id, customerDTO);
        
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<Void> deleteCustomerProfile(@PathVariable Long id) {
        
        // TODO: Implement service call to delete customer
        // customerService.deleteCustomer(id);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(
            @Valid @RequestBody CustomerPostDTO customerDTO,
            UriComponentsBuilder uriBuilder,
            HttpServletRequest request) {
        
        // TODO: Implement service call to create customer
        // CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        
        // Build location URI for created resource
        URI location = uriBuilder
                .path("/api/v1/customers/profile/{id}")
                .buildAndExpand(1L) // TODO: Use actual created customer ID
                .toUri();
        
        return ResponseEntity.created(location)
                .body(ApiResponse.success( "Customer created successfully",null, request.getRequestURI()));
    }
}
