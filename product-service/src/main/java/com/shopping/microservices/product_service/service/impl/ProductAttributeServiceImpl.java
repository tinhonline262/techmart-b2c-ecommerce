package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductAttributeMapper;
import com.shopping.microservices.product_service.mapper.ProductAttributeValueMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import com.shopping.microservices.product_service.repository.ProductAttributeValueRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
import com.shopping.microservices.product_service.service.ProductAttributeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeValueRepository attributeValueRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeMapper attributeMapper;
    private final ProductAttributeValueMapper attributeValueMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductAttributeDTO> getAttributes(Pageable pageable) {
        log.info("Fetching product attributes with pagination: {}", pageable);
        Page<ProductAttribute> page = attributeRepository.findAll(pageable);

        var dtos = page.getContent().stream()
                .map(attribute -> mapToDTO(attribute))
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
                .map(attribute -> mapToDTO(attribute))
                .orElseThrow(() -> {
                    log.warn("Product attribute not found with id: {}", id);
                    return new RuntimeException("Product attribute not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductAttributeDTO createAttribute(ProductAttributeCreationDTO dto) {
        log.info("Creating product attribute: {}", dto.name());

        // Find product
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + dto.productId()));

        // Create attribute and set product
        ProductAttribute attribute = ProductAttribute.builder()
                .product(product)
                .name(dto.name())
                .build();

        ProductAttribute saved = attributeRepository.save(attribute);
        log.info("Product attribute created with id: {}", saved.getId());

        return mapToDTO(saved);
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
        
        if (dto.name() != null && !dto.name().isBlank()) {
            attribute.setName(dto.name());
        }
        
        ProductAttribute updated = attributeRepository.save(attribute);
        log.info("Product attribute updated with id: {}", updated.getId());
        return mapToDTO(updated);
    }

    @Override
    public void deleteAttribute(Long id) {
        log.info("Deleting product attribute with id: {}", id);

        if (!attributeRepository.existsById(id)) {
            log.warn("Product attribute not found with id: {}", id);
            throw new RuntimeException("Product attribute not found with id: " + id);
        }

        attributeRepository.deleteById(id);
        log.info("Product attribute deleted with id: {}", id);
    }

    private ProductAttributeDTO mapToDTO(ProductAttribute attribute) {
        var values = attributeValueRepository.findByProductAttributeId(attribute.getId()).stream()
                .map(attributeValueMapper::toDTO)
                .collect(Collectors.toList());
        
        return new ProductAttributeDTO(
                attribute.getId(),
                attribute.getName(),
                values,
                null,
                null
        );
    }
}
