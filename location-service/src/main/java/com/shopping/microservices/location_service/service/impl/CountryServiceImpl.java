package com.shopping.microservices.location_service.service.impl;

import com.shopping.microservices.location_service.dto.CountryDTO;
import com.shopping.microservices.location_service.dto.CountryListGetDTO;
import com.shopping.microservices.location_service.dto.CountryPostDTO;
import com.shopping.microservices.location_service.entity.Country;
import com.shopping.microservices.location_service.mapper.CountryMapper;
import com.shopping.microservices.location_service.repository.CountryRepository;
import com.shopping.microservices.location_service.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CountryServiceImpl implements CountryService {
    
    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;
    
    @Override
    public Page<CountryDTO> getCountriesPaging(Pageable pageable) {
        return countryRepository.findAll(pageable)
                .map(countryMapper::toDTO);
    }
    
    @Override
    public List<CountryListGetDTO> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toListGetDTO)
                .toList();
    }
    
    @Override
    public CountryDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        return countryMapper.toDTO(country);
    }
    
    @Override
    @Transactional
    public CountryDTO createCountry(CountryPostDTO countryPostDTO) {
        Country country = countryMapper.toEntity(countryPostDTO);
        Country savedCountry = countryRepository.save(country);
        return countryMapper.toDTO(savedCountry);
    }
    
    @Override
    @Transactional
    public CountryDTO updateCountry(Long id, CountryPostDTO countryPostDTO) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        countryMapper.updateEntity(country, countryPostDTO);
        Country updatedCountry = countryRepository.save(country);
        return countryMapper.toDTO(updatedCountry);
    }
    
    @Override
    @Transactional
    public void deleteCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country not found with id: " + id));
        countryRepository.delete(country);
    }
}
