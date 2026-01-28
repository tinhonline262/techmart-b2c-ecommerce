package com.shopping.microservices.customer_service.service.impl;

import com.shopping.microservices.customer_service.dto.CustomerDTO;
import com.shopping.microservices.customer_service.dto.CustomerPostDTO;
import com.shopping.microservices.customer_service.entity.Customer;
import com.shopping.microservices.customer_service.repository.CustomerRepository;
import com.shopping.microservices.customer_service.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.shopping.microservices.customer_service.dto.CustomerListDTO;

/**
 * Implementation of CustomerService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerProfile(String username) {
        log.info("Fetching customer profile for username: {}", username);

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found with username: " + username));

        return mapToDTO(customer);
    }

    @Override
    @Transactional
    public CustomerDTO createGuestUser(CustomerPostDTO customerPostDTO) {
        log.info("Creating guest user with email: {}", customerPostDTO.getEmail());

        // Check if email already exists
        if (customerRepository.existsByEmail(customerPostDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + customerPostDTO.getEmail());
        }

        // Check if username already exists
        if (customerRepository.existsByUsername(customerPostDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + customerPostDTO.getUsername());
        }

        // Create customer (ignore password - it's not stored in this service)
        Customer customer = Customer.builder()
                .username(customerPostDTO.getUsername())
                .name(customerPostDTO.getName())
                .email(customerPostDTO.getEmail())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Guest user created successfully with id: {}", savedCustomer.getId());

        return mapToDTO(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerListDTO> getAllCustomers(Pageable pageable) {
        log.info("Fetching all customers, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Customer> customerPage = customerRepository.findAll(pageable);

        return customerPage.map(this::mapToListDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        log.info("Fetching customer by id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        return mapToDTO(customer);
    }

    @Override
    @Transactional
    public void updateCustomer(Long id, CustomerPostDTO customerPostDTO) {
        log.info("Updating customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Check if email is being changed and already exists
        if (!customer.getEmail().equals(customerPostDTO.getEmail()) &&
                customerRepository.existsByEmail(customerPostDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + customerPostDTO.getEmail());
        }

        // Check if username is being changed and already exists
        if (!customer.getUsername().equals(customerPostDTO.getUsername()) &&
                customerRepository.existsByUsername(customerPostDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + customerPostDTO.getUsername());
        }

        // Update customer fields
        customer.setUsername(customerPostDTO.getUsername());
        customer.setName(customerPostDTO.getName());
        customer.setEmail(customerPostDTO.getEmail());

        customerRepository.save(customer);
        log.info("Customer updated successfully with id: {}", id);
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        customerRepository.delete(customer);
        log.info("Customer deleted successfully with id: {}", id);
    }

    /**
     * Map Customer entity to CustomerDTO
     */
    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .name(customer.getName())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    /**
     * Map Customer entity to CustomerListDTO
     */
    private CustomerListDTO mapToListDTO(Customer customer) {
        return CustomerListDTO.builder()
                .id(customer.getId())
                .username(customer.getUsername())
                .name(customer.getName())
                .email(customer.getEmail())
                .build();
    }
    @Override
    @Transactional
    public CustomerDTO createCustomer(CustomerPostDTO customerPostDTO) {
        log.info("Creating customer with email: {}", customerPostDTO.getEmail());

        // Check if email already exists
        if (customerRepository.existsByEmail(customerPostDTO.getEmail())) {
            throw new RuntimeException("Email already exists: " + customerPostDTO.getEmail());
        }

        // Check if username already exists
        if (customerRepository.existsByUsername(customerPostDTO.getUsername())) {
            throw new RuntimeException("Username already exists: " + customerPostDTO.getUsername());
        }

        // Create customer
        Customer customer = Customer.builder()
                .username(customerPostDTO.getUsername())
                .name(customerPostDTO.getName())
                .email(customerPostDTO.getEmail())
                .build();

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully with id: {}", savedCustomer.getId());

        return mapToDTO(savedCustomer);
    }
}