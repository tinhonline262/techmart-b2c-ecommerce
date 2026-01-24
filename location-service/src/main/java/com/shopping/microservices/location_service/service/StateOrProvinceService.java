package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StateOrProvinceService {
    
    /**
     * Get all states/provinces with pagination and optional country filter
     */
    Page<StateOrProvinceDTO> getStateOrProvincesPaging(Pageable pageable, Long countryId);
    
    /**
     * Get all states/provinces with optional country filter
     */
    List<StateOrProvinceListGetDTO> getAllStateOrProvinces(Long countryId);
    
    /**
     * Get state/province by ID
     */
    StateOrProvinceDTO getStateOrProvinceById(Long id);
    
    /**
     * Get state and country names by IDs (batch operation)
     */
    List<StateCountryNameDTO> getStateCountryNames(List<Long> stateOrProvinceIds);
    
    /**
     * Create a new state/province
     */
    StateOrProvinceDTO createStateOrProvince(StateOrProvincePostDTO stateOrProvincePostDTO);
    
    /**
     * Update an existing state/province
     */
    StateOrProvinceDTO updateStateOrProvince(Long id, StateOrProvincePostDTO stateOrProvincePostDTO);
    
    /**
     * Delete a state/province
     */
    void deleteStateOrProvince(Long id);
}
