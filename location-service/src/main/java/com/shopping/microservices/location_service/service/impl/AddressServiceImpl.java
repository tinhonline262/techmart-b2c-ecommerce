package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.AddressDTO;
import com.shopping.microservices.location_service.dto.AddressPostDTO;
import com.shopping.microservices.location_service.entity.Address;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.repository.AddressRepository;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CountryRepository countryRepository;

    @Override
    @Transactional
    public AddressDTO createAddress(AddressPostDTO addressPostDTO) {
        log.info("Creating address for contact: {}", addressPostDTO.contactName());

        // Validate country exists
        Country country = countryRepository.findById(addressPostDTO.countryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + addressPostDTO.countryId()));

        Address address = Address.builder()
                .contactName(addressPostDTO.contactName())
                .phone(addressPostDTO.phone())
                .addressLine1(addressPostDTO.addressLine1())
                .addressLine2(addressPostDTO.addressLine2())
                .city(addressPostDTO.city())
                .zipCode(addressPostDTO.zipCode())
                .country(country)
                .stateOrProvinceId(addressPostDTO.stateOrProvinceId()) // THÊM
                .districtId(addressPostDTO.districtId())                 // THÊM
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with id: {}", savedAddress.getId());

        return mapToDTO(savedAddress);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long id, AddressPostDTO addressPostDTO) {
        log.info("Updating address id: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        // Validate country if changed
        if (!address.getCountry().getId().equals(addressPostDTO.countryId())) {
            Country country = countryRepository.findById(addressPostDTO.countryId())
                    .orElseThrow(() -> new RuntimeException("Country not found with id: " + addressPostDTO.countryId()));
            address.setCountry(country);
        }

        address.setContactName(addressPostDTO.contactName());
        address.setPhone(addressPostDTO.phone());
        address.setAddressLine1(addressPostDTO.addressLine1());
        address.setAddressLine2(addressPostDTO.addressLine2());
        address.setCity(addressPostDTO.city());
        address.setZipCode(addressPostDTO.zipCode());
        address.setStateOrProvinceId(addressPostDTO.stateOrProvinceId()); // THÊM
        address.setDistrictId(addressPostDTO.districtId());                 // THÊM
        address.setUpdatedAt(Instant.now());
        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully");

        return mapToDTO(updatedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public AddressDTO getAddressById(Long id) {
        log.info("Fetching address by id: {}", id);

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        return mapToDTO(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getAddressesByIds(List<Long> ids) {
        log.info("Fetching {} addresses", ids.size());

        List<Address> addresses = addressRepository.findByIdIn(ids);

        return addresses.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        log.info("Deleting address id: {}", id);

        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found with id: " + id);
        }

        addressRepository.deleteById(id);
        log.info("Address deleted successfully");
    }

    private AddressDTO mapToDTO(Address address) {
        LocalDateTime createdAt = address.getCreatedAt() != null
                ? LocalDateTime.ofInstant(address.getCreatedAt(), ZoneId.systemDefault())
                : null;

        LocalDateTime updatedAt = address.getUpdatedAt() != null
                ? LocalDateTime.ofInstant(address.getUpdatedAt(), ZoneId.systemDefault())
                : null;

        return new AddressDTO(
                address.getId(),
                address.getContactName(),
                address.getPhone(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getZipCode(),
                address.getCountry().getId(),
                address.getStateOrProvinceId(), // SỬA
                address.getDistrictId(),         // SỬA
                createdAt,
                updatedAt
        );
    }
}