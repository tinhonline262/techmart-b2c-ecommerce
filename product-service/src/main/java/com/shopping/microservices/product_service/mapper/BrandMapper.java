package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.BrandCreationDTO;
import com.shopping.microservices.product_service.dto.BrandDTO;
import com.shopping.microservices.product_service.dto.BrandDetailDTO;
import com.shopping.microservices.product_service.dto.BrandUpdateDTO;
import com.shopping.microservices.product_service.entity.Brand;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BrandMapper {

    /**
     * Map BrandCreationDTO to Brand entity
     */
    public Brand toEntity(BrandCreationDTO dto) {
        if (dto == null) return null;

        return Brand.builder()
                .name(dto.name())
                .slug(dto.slug())
                .isPublished(dto.isPublished())
                .build();
    }

    /**
     * Update Brand entity from BrandUpdateDTO
     */
    public void updateEntity(Brand brand, BrandUpdateDTO dto) {
        if (dto == null) return;

        if (dto.name() != null) brand.setName(dto.name());
        if (dto.slug() != null) brand.setSlug(dto.slug());
        if (dto.isPublished() != null) brand.setIsPublished(dto.isPublished());
    }

    /**
     * Map Brand entity to BrandDTO
     */
    public BrandDTO toDTO(Brand brand) {
        if (brand == null) return null;

        return new BrandDTO(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                null, // description
                null, // logoUrl
                Boolean.TRUE.equals(brand.getIsPublished()),
                null, // createdAt
                null  // updatedAt
        );
    }

    /**
     * Map Brand entity to BrandDetailDTO
     */
    public BrandDetailDTO toDetailDTO(Brand brand) {
        if (brand == null) return null;

        return new BrandDetailDTO(
                brand.getId(),
                brand.getName(),
                brand.getSlug(),
                null, // description
                null, // logoUrl
                Boolean.TRUE.equals(brand.getIsPublished()),
                0     // productCount
        );
    }

    /**
     * Map list of Brands to list of BrandDTOs
     */
    public List<BrandDTO> toDTOList(List<Brand> brands) {
        if (brands == null) return Collections.emptyList();
        return brands.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
