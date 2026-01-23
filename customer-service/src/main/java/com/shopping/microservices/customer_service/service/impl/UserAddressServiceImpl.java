package com.shopping.microservices.customer_service.service.impl;

import com.shopping.microservices.customer_service.dto.UserAddressDTO;
import com.shopping.microservices.customer_service.dto.UserAddressPostDTO;
import com.shopping.microservices.customer_service.entity.UserAddress;
import com.shopping.microservices.customer_service.repository.UserAddressRepository;
import com.shopping.microservices.customer_service.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of UserAddressService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressDTO> getUserAddresses(String userId) {
        log.info("Fetching addresses for user: {}", userId);

        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);

        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressDTO getDefaultAddress(String userId) {
        log.info("Fetching default address for user: {}", userId);

        // Try to get active address first
        List<UserAddress> activeAddresses = userAddressRepository.findByUserIdAndIsActive(userId, true);

        if (!activeAddresses.isEmpty()) {
            return mapToDTO(activeAddresses.get(0));
        }

        // If no active address, return first address
        List<UserAddress> allAddresses = userAddressRepository.findByUserId(userId);

        if (allAddresses.isEmpty()) {
            throw new RuntimeException("No address found for user: " + userId);
        }

        return mapToDTO(allAddresses.get(0));
    }

    @Override
    @Transactional
    public UserAddressDTO createUserAddress(String userId, UserAddressPostDTO addressPostDTO) {
        log.info("Creating address for user: {}", userId);

        // If this is set as default, deactivate all other addresses first
        if (Boolean.TRUE.equals(addressPostDTO.getIsDefault())) {
            List<UserAddress> allAddresses = userAddressRepository.findByUserId(userId);
            allAddresses.forEach(addr -> {
                addr.setIsActive(false);
                addr.setUpdatedAt(Instant.now());
            });
            userAddressRepository.saveAll(allAddresses);
        }

        UserAddress userAddress = UserAddress.builder()
                .userId(userId)
                .addressId(addressPostDTO.getAddressId())
                .isActive(Boolean.TRUE.equals(addressPostDTO.getIsDefault()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        UserAddress savedAddress = userAddressRepository.save(userAddress);
        log.info("Address created successfully with id: {}", savedAddress.getId());

        return mapToDTO(savedAddress);
    }

    @Override
    @Transactional
    public void setDefaultAddress(String userId, Long addressId) {
        log.info("Setting default address {} for user: {}", addressId, userId);

        UserAddress address = userAddressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        // Deactivate all other addresses
        List<UserAddress> allAddresses = userAddressRepository.findByUserId(userId);
        allAddresses.forEach(addr -> {
            addr.setIsActive(false);
            addr.setUpdatedAt(Instant.now());
        });
        userAddressRepository.saveAll(allAddresses);

        // Activate this address
        address.setIsActive(true);
        address.setUpdatedAt(Instant.now());
        userAddressRepository.save(address);

        log.info("Default address set successfully");
    }

    @Override
    @Transactional
    public void deleteUserAddress(String userId, Long addressId) {
        log.info("Deleting address {} for user: {}", addressId, userId);

        UserAddress address = userAddressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new RuntimeException("Address not found: " + addressId));

        userAddressRepository.delete(address);
        log.info("Address deleted successfully");
    }

    /**
     * Map UserAddress entity to UserAddressDTO
     * Entity uses isActive, DTO uses isDefault
     */
    private UserAddressDTO mapToDTO(UserAddress userAddress) {
        LocalDateTime createdAt = userAddress.getCreatedAt() != null
                ? LocalDateTime.ofInstant(userAddress.getCreatedAt(), ZoneId.systemDefault())
                : null;

        LocalDateTime updatedAt = userAddress.getUpdatedAt() != null
                ? LocalDateTime.ofInstant(userAddress.getUpdatedAt(), ZoneId.systemDefault())
                : null;

        return UserAddressDTO.builder()
                .id(userAddress.getId())
                .userId(userAddress.getUserId())
                .addressId(userAddress.getAddressId())
                .isDefault(userAddress.getIsActive()) // Map isActive -> isDefault
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}