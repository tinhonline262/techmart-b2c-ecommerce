package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductAttributeGroupMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import com.shopping.microservices.product_service.service.ProductAttributeGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductAttributeGroupServiceImpl implements ProductAttributeGroupService {

    private final ProductAttributeGroupRepository productAttributeGroupRepository;
    private final ProductAttributeGroupMapper productAttributeGroupMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductAttributeGroupDTO> getAllAttributeGroups(Pageable pageable) {
        log.info("Fetching all product attribute groups with pagination: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductAttributeGroupDTO> page = productAttributeGroupRepository.findAll(pageable)
                .map(productAttributeGroupMapper::toDTO);

        log.info("Found {} product attribute groups", page.getTotalElements());

        return new PageResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeGroupDTO getAttributeGroupById(Long id) {
        log.info("Fetching product attribute group with ID: {}", id);

        ProductAttributeGroup group = productAttributeGroupRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product attribute group not found with ID: {}", id);
                    return new ProductNotFoundException("Product attribute group not found with ID: " + id);
                });

        return productAttributeGroupMapper.toDTO(group);
    }
}
