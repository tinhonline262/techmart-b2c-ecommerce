package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.brand.BrandCreationDTO;
import com.shopping.microservices.product_service.dto.brand.BrandDTO;
import com.shopping.microservices.product_service.dto.brand.BrandDetailDTO;
import com.shopping.microservices.product_service.dto.brand.BrandUpdateDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BrandService {

    @Cacheable(value = "publishedBrands")
    @Transactional(readOnly = true)
    PageResponseDTO<BrandDTO> getAllPublishedBrands(Pageable pageable);

    // Public API methods
    List<BrandDTO> getAllPublishedBrands();
    
    BrandDetailDTO getBrandBySlug(String slug);

    @Cacheable(
            value = "brandBySlug",
            key = "#slug.toLowerCase()"
    )
    @Transactional(readOnly = true)
    BrandDetailDTO getBrandPublicBySlug(String slug);

    // Admin API methods
    PageResponseDTO<BrandDTO> getAllBrands(String keyword, Pageable pageable);
    
    BrandDTO getBrandById(Long id);
    
    BrandDTO createBrand(BrandCreationDTO brandCreationDTO);
    
    BrandDTO updateBrand(Long id, BrandUpdateDTO brandUpdateDTO);
    
    void deleteBrand(Long id);
}
