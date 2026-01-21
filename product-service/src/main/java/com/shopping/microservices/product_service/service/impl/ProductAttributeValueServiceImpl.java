package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import com.shopping.microservices.product_service.repository.ProductAttributeValueRepository;
import com.shopping.microservices.product_service.service.ProductAttributeValueService;
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
public class ProductAttributeValueServiceImpl implements ProductAttributeValueService {
    
    private final ProductAttributeValueRepository attributeValueRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValueDTO> getAttributeValues(Long attributeId) {
        log.info("Fetching product attribute values for attribute id: {}", attributeId);
        
        return attributeValueRepository.findByProductAttributeId(attributeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private ProductAttributeValueDTO mapToDTO(ProductAttributeValue value) {
        return new ProductAttributeValueDTO(
                value.getId(),
                value.getProductAttribute() != null ? value.getProductAttribute().getId() : null,
                value.getProductAttribute() != null ? value.getProductAttribute().getName() : null,
                value.getValue(),
                value.getDisplayType(),
                value.getDisplayOrder(),
                null,
                null
        );
    }
}
