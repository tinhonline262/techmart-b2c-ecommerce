package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;
import com.shopping.microservices.product_service.entity.ProductOptionValue;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.service.ProductOptionValueService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ProductOptionValueServiceImpl implements ProductOptionValueService {
    
    private final ProductOptionValueRepository optionValueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductOptionValueDTO> getOptionValues(Long optionId) {
        log.info("Fetching product option values for option id: {}", optionId);
        
        return optionValueRepository.findByProductOptionId(optionId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ProductOptionValueDTO mapToDTO(ProductOptionValue value) {
        return new ProductOptionValueDTO(
                value.getId(),
                value.getProductOption() != null ? value.getProductOption().getId() : null,
                value.getProductOption() != null ? value.getProductOption().getName() : null,
                value.getValue(),
                value.getDisplayType(),
                value.getDisplayOrder(),
                null,
                null
        );
    }
}
