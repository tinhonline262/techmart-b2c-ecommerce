package com.shopping.microservices.customer_service.repository;

import com.shopping.microservices.customer_service.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserAddress entity
 */
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {
    
    /**
     * Find all addresses for a specific user
     * @param userId the user identifier
     * @return list of user addresses
     */
    List<UserAddress> findByUserId(String userId);
    
    /**
     * Find the default address for a specific user
     * @param userId the user identifier
     * @param isDefault flag to filter default address
     * @return optional user address
     */
    Optional<UserAddress> findByUserIdAndIsDefault(String userId, Boolean isDefault);
    
    /**
     * Find a specific user address by user and address ID
     * @param userId the user identifier
     * @param id the address ID
     * @return optional user address
     */
    Optional<UserAddress> findByUserIdAndId(String userId, Long id);
    
    /**
     * Delete a specific user address
     * @param userId the user identifier
     * @param id the address ID
     */
    void deleteByUserIdAndId(String userId, Long id);
}
