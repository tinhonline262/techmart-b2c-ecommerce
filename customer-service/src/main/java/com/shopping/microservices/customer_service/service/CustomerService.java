package com.shopping.microservices.customer_service.service;

import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerListDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for Customer operations
 */
public interface CustomerService {

    /**
     * Get customer profile by username
     * @param username the username from authentication
     * @return customer profile DTO
     */
    CustomerDTO getCustomerProfile(String username);

    /**
     * Create a new guest user
     * @param customerPostDTO customer data
     * @return created customer DTO
     */
    CustomerDTO createGuestUser(CustomerPostDTO customerPostDTO);

    /**
     * Get all customers with pagination (Admin only)
     * @param pageable pagination info
     * @return page of customers
     */
    Page<CustomerListDTO> getAllCustomers(Pageable pageable);

    /**
     * Get customer by ID (Admin only)
     * @param id customer ID
     * @return customer DTO
     */
    CustomerDTO getCustomerById(Long id);

    /**
     * Update customer (Admin only)
     * @param id customer ID
     * @param customerPostDTO updated customer data
     */
    void updateCustomer(Long id, CustomerPostDTO customerPostDTO);

    /**
     * Delete customer (Admin only)
     * @param id customer ID
     */
    void deleteCustomer(Long id);
    /**
     * Create customer (Admin only)
     * @param customerPostDTO customer data
     * @return created customer DTO
     */
    CustomerDTO createCustomer(CustomerPostDTO customerPostDTO);
}