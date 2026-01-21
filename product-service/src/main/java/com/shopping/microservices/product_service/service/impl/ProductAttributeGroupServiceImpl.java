package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.AttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import com.shopping.microservices.product_service.service.ProductAttributeGroupService;
import com.shopping.microservices.product_service.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ProductAttributeGroupServiceImpl implements ProductAttributeGroupService {

    private final ProductAttributeGroupRepository attributeGroupRepository;
    private final AttributeMapper attributeMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductAttributeGroupDTO> getAttributeGroups(Pageable pageable) {
        log.info("Fetching product attribute groups with pagination: {}", pageable);
        Page<ProductOption> page = attributeGroupRepository.findAll(pageable);

        var dtos = page.getContent().stream()
                .map(attributeMapper::toAttributeGroupDTO)
                .toList();

        return new PageResponseDTO<>(
                dtos,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.isEmpty()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeGroupDTO getAttributeGroupById(Long id) {
        log.info("Fetching product attribute group with id: {}", id);
        return attributeGroupRepository.findById(id)
                .map(attributeMapper::toAttributeGroupDTO)
                .orElseThrow(() -> {
                    log.warn("Product attribute group not found with id: {}", id);
                    return new RuntimeException("Product attribute group not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductAttributeGroupDTO createAttributeGroup(ProductAttributeGroupCreationDTO dto) {
        log.info("Creating product attribute group: {}", dto.name());

        // Load Product entity
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + dto.productId()));

        // Create attribute group using ProductOption entity
        ProductOption attributeGroup = ProductOption.builder()
                .name(dto.name())
                .product(product)  // Set product reference
                .build();

        ProductOption saved = attributeGroupRepository.save(attributeGroup);
        log.info("Product attribute group created with id: {}", saved.getId());

        return attributeMapper.toAttributeGroupDTO(saved);
    }

    @Override
    @Transactional
    public ProductAttributeGroupDTO updateAttributeGroup(Long id, ProductAttributeGroupUpdateDTO dto) {
        log.info("Updating product attribute group with id: {}", id);

        ProductOption attributeGroup = attributeGroupRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product attribute group not found with id: {}", id);
                    return new RuntimeException("Product attribute group not found with id: " + id);
                });

        attributeMapper.updateAttributeGroupEntity(attributeGroup, dto);

        ProductOption updated = attributeGroupRepository.save(attributeGroup);
        log.info("Product attribute group updated with id: {}", updated.getId());

        return attributeMapper.toAttributeGroupDTO(updated);
    }

    @Override
    @Transactional
    public void deleteAttributeGroup(Long id) {
        log.info("Deleting product attribute group with id: {}", id);

        if (!attributeGroupRepository.existsById(id)) {
            log.warn("Product attribute group not found with id: {}", id);
            throw new RuntimeException("Product attribute group not found with id: " + id);
        }

        attributeGroupRepository.deleteById(id);
        log.info("Product attribute group deleted with id: {}", id);
    }
}
