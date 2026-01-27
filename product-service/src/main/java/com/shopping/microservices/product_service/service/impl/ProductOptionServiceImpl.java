package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductOptionCreationDTO;
import com.shopping.microservices.product_service.dto.ProductOptionDTO;
import com.shopping.microservices.product_service.dto.ProductOptionUpdateDTO;
import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.entity.ProductOptionValue;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.repository.ProductOptionRepository;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
import com.shopping.microservices.product_service.service.ProductOptionService;
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
public class ProductOptionServiceImpl implements ProductOptionService {
    
    private final ProductOptionRepository optionRepository;
    private final ProductOptionValueRepository optionValueRepository;
    private final ProductRepository productRepository;
    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<ProductOptionDTO> getOptions(Pageable pageable) {
        log.info("Fetching product options with pagination: {}", pageable);
        Page<ProductOption> page = optionRepository.findAll(pageable);

        var dtos = page.getContent().stream()
                .map(this::mapToDTO)
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
    public ProductOptionDTO getOptionById(Long id) {
        log.info("Fetching product option with id: {}", id);
        return optionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> {
                    log.warn("Product option not found with id: {}", id);
                    return new RuntimeException("Product option not found with id: " + id);
                });
    }

    @Override
    @Transactional
    public ProductOptionDTO createOption(ProductOptionCreationDTO dto) {
        log.info("Creating product option: {}", dto.name());

        // 1. Tìm product
        Product product = productRepository.findById(dto.productId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found: " + dto.productId()));

        // 2. Tạo option VÀ set product
        ProductOption option = ProductOption.builder()
                .product(product)  // ← THÊM DÒ NÀY
                .name(dto.name())
                .build();

        ProductOption saved = optionRepository.save(option);
        log.info("Product option created with id: {}", saved.getId());

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ProductOptionDTO updateOption(Long id, ProductOptionUpdateDTO dto) {
        log.info("Updating product option with id: {}", id);
        
        ProductOption option = optionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product option not found with id: {}", id);
                    return new RuntimeException("Product option not found with id: " + id);
                });
        
        if (dto.name() != null && !dto.name().isBlank()) {
            option.setName(dto.name());
        }
        
        ProductOption updated = optionRepository.save(option);
        log.info("Product option updated with id: {}", updated.getId());
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteOption(Long id) {
        log.info("Deleting product option with id: {}", id);
        if (!optionRepository.existsById(id)) {
            log.warn("Product option not found with id: {}", id);
            throw new RuntimeException("Product option not found with id: " + id);
        }
        optionRepository.deleteById(id);
        log.info("Product option deleted with id: {}", id);
    }

    private ProductOptionDTO mapToDTO(ProductOption option) {
        var values = optionValueRepository.findByProductOptionId(option.getId()).stream()
                .map(this::mapValueToDTO)
                .collect(Collectors.toList());
        
        return new ProductOptionDTO(
                option.getId(),
                option.getName(),
                values,
                null,
                null
        );
    }

    private ProductOptionValueDTO mapValueToDTO(ProductOptionValue value) {
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
