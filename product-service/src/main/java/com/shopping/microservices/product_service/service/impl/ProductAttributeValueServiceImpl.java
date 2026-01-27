package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.mapper.ProductAttributeValueMapper;
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
    private final ProductAttributeValueMapper attributeValueMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeValueDTO> getAttributeValues(Long attributeId) {
        log.info("Fetching product attribute values for attribute id: {}", attributeId);

        return attributeValueRepository.findByProductAttributeId(attributeId).stream()
                .map(attributeValueMapper::toDTO)
                .collect(Collectors.toList());
    }
}