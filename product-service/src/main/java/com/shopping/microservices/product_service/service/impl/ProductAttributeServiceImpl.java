package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.mapper.ProductAttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import com.shopping.microservices.product_service.service.ProductAttributeService;
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
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeGroupRepository attributeGroupRepository;
    private final ProductAttributeMapper attributeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductAttributeDTO> getAttributes(Pageable pageable) {
        log.info("Fetching product attributes with pagination: {}", pageable);
        Page<ProductAttribute> page = attributeRepository.findAll(pageable);

        var dtos = page.getContent().stream()
                .map(attributeMapper::toDTO)
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
    public ProductAttributeDTO getAttributeById(Long id) {
        log.info("Fetching product attribute with id: {}", id);
        return attributeRepository.findById(id)
                .map(attributeMapper::toDTO)
                .orElseThrow(() -> {
                    log.warn("Product attribute not found with id: {}", id);
                    return new RuntimeException("Product attribute not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductAttributeDTO createAttribute(ProductAttributeCreationDTO dto) {
        log.info("Creating product attribute: {}", dto.name());

        ProductAttributeGroup group = null;
        if (dto.groupId() != null) {
            group = attributeGroupRepository.findById(dto.groupId())
                    .orElseThrow(() -> {
                        log.warn("Product attribute group not found with id: {}", dto.groupId());
                        return new RuntimeException("Product attribute group not found with id: " + dto.groupId());
                    });
        }

        ProductAttribute attribute = attributeMapper.toEntity(dto, group);
        ProductAttribute saved = attributeRepository.save(attribute);
        log.info("Product attribute created with id: {}", saved.getId());

        return attributeMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ProductAttributeDTO updateAttribute(Long id, ProductAttributeUpdateDTO dto) {
        log.info("Updating product attribute with id: {}", id);
        
        ProductAttribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product attribute not found with id: {}", id);
                    return new RuntimeException("Product attribute not found with id: " + id);
                });
        
        if (dto.groupId() != null) {
            ProductAttributeGroup group = attributeGroupRepository.findById(dto.groupId())
                    .orElseThrow(() -> {
                        log.warn("Product attribute group not found with id: {}", dto.groupId());
                        return new RuntimeException("Product attribute group not found with id: " + dto.groupId());
                    });
            attribute.setProductAttributeGroup(group);
        }
        
        attributeMapper.updateEntity(attribute, dto);
        ProductAttribute updated = attributeRepository.save(attribute);
        log.info("Product attribute updated with id: {}", updated.getId());
        
        return attributeMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteAttribute(Long id) {
        log.info("Deleting product attribute with id: {}", id);

        if (!attributeRepository.existsById(id)) {
            log.warn("Product attribute not found with id: {}", id);
            throw new RuntimeException("Product attribute not found with id: " + id);
        }

        attributeRepository.deleteById(id);
        log.info("Product attribute deleted with id: {}", id);
    }
}
