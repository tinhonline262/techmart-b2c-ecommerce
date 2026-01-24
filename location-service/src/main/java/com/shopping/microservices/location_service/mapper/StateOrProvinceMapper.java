package com.shopping.microservices.location_service.mapper;

import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.entity.StateOrProvince;
import com.shopping.microservices.location_service.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
@RequiredArgsConstructor
public class StateOrProvinceMapper {
    
    private final CountryRepository countryRepository;
    
    /**
     * Convert StateOrProvince entity to StateOrProvinceDTO
     */
    public StateOrProvinceDTO toDTO(StateOrProvince stateOrProvince) {
        if (stateOrProvince == null) {
            return null;
        }
        
        return new StateOrProvinceDTO(
                stateOrProvince.getId(),
                stateOrProvince.getCode(),
                stateOrProvince.getName(),
                stateOrProvince.getType(),
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getId() : null,
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getName() : null,
                stateOrProvince.getCreatedAt() != null ? stateOrProvince.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                stateOrProvince.getUpdatedAt() != null ? stateOrProvince.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null
        );
    }
    
    /**
     * Convert StateOrProvince entity to StateOrProvinceListGetDTO
     */
    public StateOrProvinceListGetDTO toListGetDTO(StateOrProvince stateOrProvince) {
        if (stateOrProvince == null) {
            return null;
        }
        
        return new StateOrProvinceListGetDTO(
                stateOrProvince.getId(),
                stateOrProvince.getCode(),
                stateOrProvince.getName(),
                stateOrProvince.getType(),
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getId() : null
        );
    }
    
    /**
     * Convert StateOrProvince to StateCountryNameDTO (for batch operation)
     */
    public StateCountryNameDTO toStateCountryNameDTO(StateOrProvince stateOrProvince) {
        if (stateOrProvince == null) {
            return null;
        }
        
        return new StateCountryNameDTO(
                stateOrProvince.getId(),
                stateOrProvince.getName(),
                stateOrProvince.getCountry() != null ? stateOrProvince.getCountry().getName() : null
        );
    }
    
    /**
     * Convert StateOrProvincePostDTO to StateOrProvince entity (for creation)
     */
    public StateOrProvince toEntity(StateOrProvincePostDTO stateOrProvincePostDTO) {
        if (stateOrProvincePostDTO == null) {
            return null;
        }
        
        Country country = countryRepository.findById(stateOrProvincePostDTO.countryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + stateOrProvincePostDTO.countryId()));
        
        return StateOrProvince.builder()
                .code(stateOrProvincePostDTO.code())
                .name(stateOrProvincePostDTO.name())
                .type(stateOrProvincePostDTO.type())
                .country(country)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
    
    /**
     * Update StateOrProvince entity from StateOrProvincePostDTO
     */
    public void updateEntity(StateOrProvince stateOrProvince, StateOrProvincePostDTO stateOrProvincePostDTO) {
        if (stateOrProvince == null || stateOrProvincePostDTO == null) {
            return;
        }
        
        stateOrProvince.setCode(stateOrProvincePostDTO.code());
        stateOrProvince.setName(stateOrProvincePostDTO.name());
        stateOrProvince.setType(stateOrProvincePostDTO.type());
        
        Country country = countryRepository.findById(stateOrProvincePostDTO.countryId())
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + stateOrProvincePostDTO.countryId()));
        stateOrProvince.setCountry(country);
        
        stateOrProvince.setUpdatedAt(Instant.now());
    }
}
