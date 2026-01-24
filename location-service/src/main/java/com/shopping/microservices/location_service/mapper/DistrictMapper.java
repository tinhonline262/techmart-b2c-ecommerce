package com.shopping.microservices.location_service.mapper;

import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.entity.District;
import org.springframework.stereotype.Component;

@Component
public class DistrictMapper {
    
    /**
     * Convert District entity to DistrictListGetDTO
     */
    public DistrictListGetDTO toListGetDTO(District district) {
        if (district == null) {
            return null;
        }
        
        return new DistrictListGetDTO(
                district.getId(),
                district.getName(),
                district.getType(),
                district.getLocation()
        );
    }
}
