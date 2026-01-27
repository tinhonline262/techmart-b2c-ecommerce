package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.AddressDTO;
import com.shopping.microservices.location_service.dto.AddressPostDTO;

import java.util.List;

/**
 * Service interface for Address operations
 */
public interface AddressService {

    /**
     * Create a new address
     */
    AddressDTO createAddress(AddressPostDTO addressPostDTO);

    /**
     * Update an existing address
     */
    AddressDTO updateAddress(Long id, AddressPostDTO addressPostDTO);

    /**
     * Get address by ID
     */
    AddressDTO getAddressById(Long id);

    /**
     * Get multiple addresses by IDs
     */
    List<AddressDTO> getAddressesByIds(List<Long> ids);

    /**
     * Delete address by ID
     */
    void deleteAddress(Long id);
}