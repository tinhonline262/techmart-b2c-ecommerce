package com.shopping.microservices.location_service.service;

import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.dto.DistrictPostDTO;
import com.shopping.microservices.location_service.dto.DistrictUpdateDTO;

import java.util.List;

public interface DistrictService {
    
    /**
     * Get districts by state/province ID
     */
    List<DistrictListGetDTO> getDistrictsByStateOrProvinceId(Long stateOrProvinceId);
}
