package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.DistrictListGetDTO;
import com.shopping.microservices.location_service.mapper.DistrictMapper;
import com.shopping.microservices.location_service.repository.DistrictRepository;
import com.shopping.microservices.location_service.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {
    
    private final DistrictRepository districtRepository;
    private final DistrictMapper districtMapper;
    
    @Override
    public List<DistrictListGetDTO> getDistrictsByStateOrProvinceId(Long stateOrProvinceId) {
        return districtRepository.findByStateOrProvinceId(stateOrProvinceId).stream()
                .map(districtMapper::toListGetDTO)
                .toList();
    }
}
