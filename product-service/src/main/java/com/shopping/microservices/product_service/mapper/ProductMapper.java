package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.entity.*;
import com.shopping.microservices.product_service.repository.ProductCategoryRepository;
import com.shopping.microservices.product_service.repository.ProductImageRepository;
import com.shopping.microservices.product_service.repository.ProductOptionRepository;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.repository.ProductOptionCombinationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final BrandMapper brandMapper;
    private final CategoryMapper categoryMapper;
    private final ProductImageMapper productImageMapper;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductOptionCombinationRepository productOptionCombinationRepository;
    private final ProductOptionValueMapper productOptionValueMapper;
    private final ProductOptionCombinationMapper productOptionCombinationMapper;

    /**
     * Map ProductCreationDTO to Product entity
     */
    public Product toEntity(ProductCreationDTO dto) {
        if (dto == null) return null;

        return Product.builder()
                .name(dto.name())
                .slug(dto.slug())
                .sku(dto.sku())
                .shortDescription(dto.shortDescription())
                .description(dto.description())
                .specification(dto.specification())
                .price(dto.price())
                .stockQuantity(dto.stockQuantity() != null ? Long.valueOf(dto.stockQuantity()) : 0L)
                .stockTrackingEnabled(dto.stockTrackingEnabled() != null ? dto.stockTrackingEnabled() : true)
                .isAllowedToOrder(dto.isAllowedToOrder() != null ? dto.isAllowedToOrder() : true)
                .isPublished(dto.isPublished() != null ? dto.isPublished() : false)
                .isFeatured(dto.isFeatured() != null ? dto.isFeatured() : false)
                .isVisibleIndividually(dto.isVisibleIndividually() != null ? dto.isVisibleIndividually() : true)
                .brandId(dto.brandId())
                .metaTitle(dto.metaTitle())
                .metaKeyword(dto.metaKeywords())
                .metaDescription(dto.metaDescription())
                .weight(dto.weight())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Update Product entity from ProductUpdateDTO
     */
    public void updateEntity(Product product, ProductUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) product.setName(dto.name());
        if (dto.slug() != null) product.setSlug(dto.slug());
        if (dto.sku() != null) product.setSku(dto.sku());
        if (dto.shortDescription() != null) product.setShortDescription(dto.shortDescription());
        if (dto.description() != null) product.setDescription(dto.description());
        if (dto.specification() != null) product.setSpecification(dto.specification());
        if (dto.price() != null) product.setPrice(dto.price());
        if (dto.stockQuantity() != null) product.setStockQuantity(Long.valueOf(dto.stockQuantity()));
        if (dto.stockTrackingEnabled() != null) product.setStockTrackingEnabled(dto.stockTrackingEnabled());
        if (dto.isAllowedToOrder() != null) product.setIsAllowedToOrder(dto.isAllowedToOrder());
        if (dto.isPublished() != null) product.setIsPublished(dto.isPublished());
        if (dto.isFeatured() != null) product.setIsFeatured(dto.isFeatured());
        if (dto.isVisibleIndividually() != null) product.setIsVisibleIndividually(dto.isVisibleIndividually());
        if (dto.brandId() != null) product.setBrandId(dto.brandId());
        if (dto.metaTitle() != null) product.setMetaTitle(dto.metaTitle());
        if (dto.metaKeywords() != null) product.setMetaKeyword(dto.metaKeywords());
        if (dto.metaDescription() != null) product.setMetaDescription(dto.metaDescription());
        if (dto.weight() != null) product.setWeight(dto.weight());
        
        product.setUpdatedAt(Instant.now());
    }

    /**
     * Map Product entity to ProductDTO
     */
    public ProductDTO toDTO(Product product) {
        if (product == null) return null;

        // load categories associated with the product
        var categories = productCategoryRepository.findByProductId(product.getId()).stream()
                .map(pc -> categoryMapper.toDTO(pc.getCategory()))
                .collect(Collectors.toList());

        // load images associated with the product
        var images = productImageRepository.findByProductId(product.getId()).stream()
                .map(productImageMapper::toDTO)
                .collect(Collectors.toList());

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getSku(),
                product.getShortDescription(),
                product.getDescription(),
                product.getSpecification(),
                product.getPrice(),
                null, // oldPrice
                null, // specialPrice
                null, // cost
                product.getStockQuantity() != null ? product.getStockQuantity().intValue() : 0,
                Boolean.TRUE.equals(product.getStockTrackingEnabled()),
                Boolean.TRUE.equals(product.getIsAllowedToOrder()),
                Boolean.TRUE.equals(product.getIsPublished()),
                Boolean.TRUE.equals(product.getIsFeatured()),
                Boolean.TRUE.equals(product.getIsVisibleIndividually()),
                null, // brand - to be loaded separately
                categories,
                images,
                product.getMetaTitle(),
                product.getMetaDescription(),
                product.getMetaKeyword(),
                null, // thumbnailUrl
                product.getWeight(),
                null, // dimensions
                toLocalDateTime(product.getCreatedAt()),
                toLocalDateTime(product.getUpdatedAt())
        );
    }

    /**
     * Map Product entity to ProductSummaryDTO
     */
    public ProductSummaryDTO toSummaryDTO(Product product) {
        if (product == null) return null;

        return new ProductSummaryDTO(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getSku(),
                product.getPrice(),
                null, // oldPrice
                null, // specialPrice
                null, // thumbnailUrl
                null, // brandName
                product.getStockQuantity() != null ? product.getStockQuantity().intValue() : 0,
                product.getStockQuantity() != null && product.getStockQuantity() > 0,
                Boolean.TRUE.equals(product.getIsPublished()),
                Boolean.TRUE.equals(product.getIsFeatured()),
                null, // averageRating
                null  // reviewCount
        );
    }

    /**
     * Map Product entity to ProductDetailDTO
     */
    public ProductDetailDTO toDetailDTO(Product product) {
        if (product == null) return null;

        Long productId = product.getId();

        // categories
        var categories = productCategoryRepository.findByProductId(productId).stream()
                .map(pc -> categoryMapper.toDTO(pc.getCategory()))
                .collect(Collectors.toList());

        // images
        var images = productImageRepository.findByProductId(productId).stream()
                .map(productImageMapper::toDTO)
                .collect(Collectors.toList());

        // options with values
        var options = productOptionRepository.findByProductId(productId);
        var optionDTOs = options.stream().map(opt -> {
            var values = productOptionValueRepository.findByProductOptionId(opt.getId());
            var valueDTOs = productOptionValueMapper.toDTOList(values);
            return new com.shopping.microservices.product_service.dto.ProductOptionDTO(
                    opt.getId(),
                    opt.getName(),
                    valueDTOs,
                    null,
                    null
            );
        }).collect(Collectors.toList());

        // build a lookup for option values by (optionName,value)
        var allOptionValues = productOptionValueRepository.findByProductOptionProductId(productId);
        var optionValueLookup = allOptionValues.stream()
                .map(productOptionValueMapper::toDTO)
                .collect(Collectors.toMap(v -> v.optionName() + "::" + v.value(), v -> v));

        // combinations with option values
        var combinations = productOptionCombinationRepository.findByProductId(productId);
        var combinationDTOs = combinations.stream().map(c -> {
            List<com.shopping.microservices.product_service.dto.ProductOptionValueDTO> combValues = new ArrayList<>();
            if (c.getValue() != null && !c.getValue().isBlank()) {
                var parts = c.getValue().split(";");
                for (String part : parts) {
                    var kv = part.split("=", 2);
                    if (kv.length == 2) {
                        var optName = kv[0].trim();
                        var val = kv[1].trim();
                        var key = optName + "::" + val;
                        var v = optionValueLookup.get(key);
                        if (v != null) combValues.add(v);
                    }
                }
            }
            return new com.shopping.microservices.product_service.dto.ProductOptionCombinationDTO(
                    c.getId(),
                    c.getProduct() != null ? c.getProduct().getId() : null,
                    c.getSku(),
                    null,
                    null,
                    null,
                    combValues,
                    null,
                    null
            );
        }).collect(Collectors.toList());

        return new ProductDetailDTO(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getSku(),
                product.getShortDescription(),
                product.getDescription(),
                product.getSpecification(),
                product.getPrice(),
                null, // oldPrice
                null, // specialPrice
                product.getStockQuantity() != null ? product.getStockQuantity().intValue() : 0,
                product.getStockQuantity() != null && product.getStockQuantity() > 0,
                Boolean.TRUE.equals(product.getIsPublished()),
                Boolean.TRUE.equals(product.getIsFeatured()),
                null, // brand
                categories,
                images,
                optionDTOs,
                Collections.emptyList(), // attributes (not implemented yet)
                product.getMetaTitle(),
                product.getMetaDescription(),
                product.getMetaKeyword(),
                null, // thumbnailUrl
                product.getWeight(),
                null, // dimensions
                null, // averageRating
                null, // reviewCount
                toLocalDateTime(product.getCreatedAt()),
                toLocalDateTime(product.getUpdatedAt())
        );
    }

    /**
     * Map Product entity to FeaturedProductDTO
     */
    public FeaturedProductDTO toFeaturedDTO(Product product) {
        if (product == null) return null;

        return new FeaturedProductDTO(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getPrice(),
                null, // oldPrice
                null, // specialPrice
                null, // thumbnailUrl
                null, // brandName
                null, // averageRating
                null, // reviewCount
                product.getStockQuantity() != null && product.getStockQuantity() > 0,
                false // hasDiscount
        );
    }

    /**
     * Map Product entity to WarehouseProductDTO
     */
    public WarehouseProductDTO toWarehouseDTO(Product product) {
        if (product == null) return null;

        return new WarehouseProductDTO(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getStockQuantity() != null ? product.getStockQuantity().intValue() : 0,
                0, // reservedQuantity
                product.getStockQuantity() != null ? product.getStockQuantity().intValue() : 0, // availableQuantity
                null, // cost
                null, // warehouseLocation
                product.getStockQuantity() != null && product.getStockQuantity() < 10, // lowStockAlert
                10, // reorderPoint
                50  // reorderQuantity
        );
    }

    /**
     * Convert Instant to LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Instant instant) {
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Map list of Products to list of ProductDTOs
     */
    public List<ProductDTO> toDTOList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Map list of Products to list of ProductSummaryDTOs
     */
    public List<ProductSummaryDTO> toSummaryDTOList(List<Product> products) {
        if (products == null) return Collections.emptyList();
        return products.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
}
