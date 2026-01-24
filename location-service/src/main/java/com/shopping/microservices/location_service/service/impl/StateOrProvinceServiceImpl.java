package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.StateCountryNameDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceDTO;
import com.shopping.microservices.location_service.dto.StateOrProvinceListGetDTO;
import com.shopping.microservices.location_service.dto.StateOrProvincePostDTO;
import com.shopping.microservices.location_service.entity.StateOrProvince;
import com.shopping.microservices.location_service.mapper.StateOrProvinceMapper;
import com.shopping.microservices.location_service.repository.StateOrProvinceRepository;
import com.shopping.microservices.location_service.service.StateOrProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StateOrProvinceServiceImpl implements StateOrProvinceService {
    
    private final StateOrProvinceRepository stateOrProvinceRepository;
    private final StateOrProvinceMapper stateOrProvinceMapper;
    
    @Override
    public Page<StateOrProvinceDTO> getStateOrProvincesPaging(Pageable pageable, Long countryId) {
        Page<StateOrProvince> page;
        if (countryId != null) {
            page = stateOrProvinceRepository.findByCountryId(countryId, pageable);
        } else {
            page = stateOrProvinceRepository.findAll(pageable);
        }
        return page.map(stateOrProvinceMapper::toDTO);
    }
    
    @Override
    public List<StateOrProvinceListGetDTO> getAllStateOrProvinces(Long countryId) {
        List<StateOrProvince> stateOrProvinces;
        if (countryId != null) {
            stateOrProvinces = stateOrProvinceRepository.findByCountryId(countryId);
        } else {
            stateOrProvinces = stateOrProvinceRepository.findAll();
        }
        return stateOrProvinces.stream()
                .map(stateOrProvinceMapper::toListGetDTO)
                .toList();
    }
    
    @Override
    public StateOrProvinceDTO getStateOrProvinceById(Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        return stateOrProvinceMapper.toDTO(stateOrProvince);
    }
    
    @Override
    public List<StateCountryNameDTO> getStateCountryNames(List<Long> stateOrProvinceIds) {
        List<StateOrProvince> stateOrProvinces = stateOrProvinceRepository.findByIdIn(stateOrProvinceIds);
        return stateOrProvinces.stream()
                .map(stateOrProvinceMapper::toStateCountryNameDTO)
                .toList();
    }
    
    @Override
    @Transactional
    public StateOrProvinceDTO createStateOrProvince(StateOrProvincePostDTO stateOrProvincePostDTO) {
        StateOrProvince stateOrProvince = stateOrProvinceMapper.toEntity(stateOrProvincePostDTO);
        StateOrProvince savedStateOrProvince = stateOrProvinceRepository.save(stateOrProvince);
        return stateOrProvinceMapper.toDTO(savedStateOrProvince);
    }
    
    @Override
    @Transactional
    public StateOrProvinceDTO updateStateOrProvince(Long id, StateOrProvincePostDTO stateOrProvincePostDTO) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        stateOrProvinceMapper.updateEntity(stateOrProvince, stateOrProvincePostDTO);
        StateOrProvince updatedStateOrProvince = stateOrProvinceRepository.save(stateOrProvince);
        return stateOrProvinceMapper.toDTO(updatedStateOrProvince);
    }
    
    @Override
    @Transactional
    public void deleteStateOrProvince(Long id) {
        StateOrProvince stateOrProvince = stateOrProvinceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("State/Province not found with id: " + id));
        stateOrProvinceRepository.delete(stateOrProvince);
    }
}
