package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.exception.ProductAttributeNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductAttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import com.shopping.microservices.product_service.service.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeMapper productAttributeMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductAttributeDTO> getAllAttributes(Pageable pageable) {
        log.info("Fetching all product attributes with pagination: page={}, size={}", pageable.getPageNumber(),
                pageable.getPageSize());

        Page<ProductAttributeDTO> page = productAttributeRepository.findAll(pageable)
                .map(productAttributeMapper::toDTO);

        log.info("Found {} product attributes", page.getTotalElements());

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
    public ProductAttributeDTO getAttributeById(Long id) {
        log.info("Fetching product attribute with ID: {}", id);

        ProductAttribute attribute = productAttributeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product attribute not found with ID: {}", id);
                    return new ProductAttributeNotFoundException("Product attribute not found with ID: " + id);
                });

        return productAttributeMapper.toDTO(attribute);
    }

    @Override
    @Transactional
    public ProductAttributeDTO createAttribute(ProductAttributeDTO dto) {
        log.info("Creating new product attribute with name: {}", dto.name());

        if (productAttributeRepository.existsByName(dto.name())) {
            log.error("Product attribute with name already exists: {}", dto.name());
            throw new IllegalArgumentException("Product attribute with name '" + dto.name() + "' already exists");
        }

        ProductAttribute attribute = productAttributeMapper.toEntity(dto);
        ProductAttribute savedAttribute = productAttributeRepository.save(attribute);

        log.info("Product attribute created successfully with ID: {}", savedAttribute.getId());

        return productAttributeMapper.toDTO(savedAttribute);
    }

    @Override
    @Transactional
    public ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO dto) {
        log.info("Updating product attribute with ID: {}", id);

        ProductAttribute attribute = productAttributeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product attribute not found with ID: {}", id);
                    return new ProductAttributeNotFoundException("Product attribute not found with ID: " + id);
                });

        if (!attribute.getName().equals(dto.name()) && productAttributeRepository.existsByName(dto.name())) {
            log.error("Product attribute with name already exists: {}", dto.name());
            throw new IllegalArgumentException("Product attribute with name '" + dto.name() + "' already exists");
        }

        productAttributeMapper.updateEntity(dto, attribute);
        ProductAttribute updatedAttribute = productAttributeRepository.save(attribute);

        log.info("Product attribute updated successfully with ID: {}", updatedAttribute.getId());

        return productAttributeMapper.toDTO(updatedAttribute);
    }

    @Override
    @Transactional
    public void deleteAttribute(Long id) {
        log.info("Deleting product attribute with ID: {}", id);

        if (!productAttributeRepository.existsById(id)) {
            log.error("Product attribute not found with ID: {}", id);
            throw new ProductAttributeNotFoundException("Product attribute not found with ID: " + id);
        }

        productAttributeRepository.deleteById(id);

        log.info("Product attribute deleted successfully with ID: {}", id);
    }
}
