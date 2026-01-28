package com.shopping.microservices.customer_service.service;

import com.shopping.microservices.customer_service.dto.UserAddressDTO;
import com.shopping.microservices.customer_service.dto.UserAddressPostDTO;

import java.util.List;

/**
 * Service interface for UserAddress operations
 */
public interface UserAddressService {

    /**
     * Get all addresses for a user
     * @param userId the user identifier
     * @return list of user addresses
     */
    List<UserAddressDTO> getUserAddresses(String userId);

    /**
     * Get default (first active) address for a user
     * @param userId the user identifier
     * @return default user address
     */
    UserAddressDTO getDefaultAddress(String userId);

    /**
     * Create a new user address
     * @param userId the user identifier
     * @param addressPostDTO address data
     * @return created user address
     */
    UserAddressDTO createUserAddress(String userId, UserAddressPostDTO addressPostDTO);

    /**
     * Set an address as default (activate it)
     * @param userId the user identifier
     * @param addressId the address ID
     */
    void setDefaultAddress(String userId, Long addressId);

    /**
     * Delete a user address
     * @param userId the user identifier
     * @param addressId the address ID
     */
    void deleteUserAddress(String userId, Long addressId);
}