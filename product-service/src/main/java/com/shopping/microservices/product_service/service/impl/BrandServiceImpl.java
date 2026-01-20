package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.brand.BrandCreationDTO;
import com.shopping.microservices.product_service.dto.brand.BrandDTO;
import com.shopping.microservices.product_service.dto.brand.BrandDetailDTO;
import com.shopping.microservices.product_service.dto.brand.BrandUpdateDTO;
import com.shopping.microservices.product_service.entity.Brand;
import com.shopping.microservices.product_service.exception.BrandNotFoundException;
import com.shopping.microservices.product_service.mapper.BrandMapper;
import com.shopping.microservices.product_service.repository.BrandRepository;
import com.shopping.microservices.product_service.service.BrandService;
import com.shopping.microservices.product_service.util.ProductConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandServiceImpl implements BrandService {
    
    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;
    
    @Cacheable(value = "publishedBrands")
    @Transactional(readOnly = true)
    @Override
    public PageResponseDTO<BrandDTO> getAllPublishedBrands(Pageable pageable) {
        log.info("Fetching all published brands");
        Page<Brand> brandPage = brandRepository.findByIsPublishedTrue(pageable);
        List<BrandDTO> brandDTOs = brandMapper.toDTOList(brandPage.getContent());
        return new PageResponseDTO<>(
                brandDTOs,
                brandPage.getNumber(),
                brandPage.getSize(),
                brandPage.getTotalElements(),
                brandPage.getTotalPages(),
                brandPage.isFirst(),
                brandPage.isLast(),
                brandPage.isEmpty()
        );

    }

    @Override
    public List<BrandDTO> getAllPublishedBrands() {
        log.info("Fetching all published brands");
        List<Brand> brands = brandRepository.findByIsPublishedTrue();
        return brandMapper.toDTOList(brands);
    }

    @Override
    @Transactional(readOnly = true)
    public BrandDetailDTO getBrandBySlug(String slug) {
        log.info("Fetching brand by slug: {}", slug);
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new BrandNotFoundException("Brand not found with slug: " + slug));
        

        // Calculate product count
        int productCount = 0; // Placeholder for actual product count retrieval logic
        return brandMapper.toDetailDTO(brand, productCount);
    }

    @Cacheable(
            value = "brandBySlug",
            key = "#slug.toLowerCase()"
    )
    @Transactional(readOnly = true)
    @Override
    public BrandDetailDTO getBrandPublicBySlug(String slug) {
        log.info("Fetching published brand by slug: {}", slug);
        Brand brand = brandRepository.findBrandsBySlugIsAndIsPublishedTrue(slug);
        if (brand == null) {
            throw new BrandNotFoundException("Published brand not found with slug: " + slug);
        }

        // Calculate product count
        int productCount = 0; // Placeholder for actual product count retrieval logic
        return brandMapper.toDetailDTO(brand, productCount);
    }

    @Override
    @Cacheable(
            value = "brands",
            key = "T(String).valueOf(#keyword == null ? 'all' : #keyword.trim().toLowerCase())"
                    + " + ':' + #pageable.pageNumber"
                    + " + ':' + #pageable.pageSize"
                    + " + ':' + #pageable.sort.toString()"
    )
    @Transactional(readOnly = true)
    public PageResponseDTO<BrandDTO> getAllBrands(String keyword, Pageable pageable) {
        log.info("Fetching all brands with keyword: {}, page: {}", keyword, pageable.getPageNumber());
        
        Page<Brand> brandPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            brandPage = brandRepository.findByNameContainingIgnoreCase(keyword, pageable);
        } else {
            brandPage = brandRepository.findAll(pageable);
        }
        
        List<BrandDTO> brandDTOs = brandMapper.toDTOList(brandPage.getContent());
        
        return new PageResponseDTO<>(
                brandDTOs,
                brandPage.getNumber(),
                brandPage.getSize(),
                brandPage.getTotalElements(),
                brandPage.getTotalPages(),
                brandPage.isFirst(),
                brandPage.isLast(),
                brandPage.isEmpty()
        );
    }
    
    @Override
    @Cacheable(value = "brandById", key = "#id")
    @Transactional(readOnly = true)
    public BrandDTO getBrandById(Long id) {
        log.info("Fetching brand by id: {}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        return brandMapper.toDTO(brand);
    }
    
    @Override
    @CacheEvict(value = {"brands", "brandById", "brandBySlug", "publishedBrands"}, allEntries = true)
    @Transactional
    public BrandDTO createBrand(BrandCreationDTO brandCreationDTO) {
        log.info("Creating brand: {}", brandCreationDTO.name());
        // Generate slug if not provided
        String slug = brandCreationDTO.slug();
        if (brandCreationDTO.slug() == null || brandCreationDTO.slug().trim().isEmpty()) {
              slug = ProductConverter.toSlug(brandCreationDTO.name());
        }
        
        // Validate unique constraints
        if (slug != null && brandRepository.existsBySlug(slug)) {
            throw new DataIntegrityViolationException("Brand with slug '" + slug + "' already exists");
        }
        
        if (brandRepository.existsByName(brandCreationDTO.name())) {
            throw new DataIntegrityViolationException("Brand with name '" + brandCreationDTO.name() + "' already exists");
        }
        
        Brand brand = brandMapper.toEntity(brandCreationDTO);
        if (brand.getSlug() == null || brand.getSlug().trim().isEmpty()) {
            brand.setSlug(slug);
        }
        Brand savedBrand = brandRepository.save(brand);
        log.info("Brand created with id: {}", savedBrand.getId());
        
        return brandMapper.toDTO(savedBrand);
    }
    
    @Override
    @CacheEvict(value = {"brands", "brandById", "brandBySlug", "publishedBrands"}, allEntries = true)
    @Transactional
    public BrandDTO updateBrand(Long id, BrandUpdateDTO brandUpdateDTO) {
        log.info("Updating brand with id: {}", id);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        
        // Validate unique constraints if slug is being updated
        if (brandUpdateDTO.slug() != null && !brandUpdateDTO.slug().equals(brand.getSlug())) {
            if (brandRepository.existsBySlug(brandUpdateDTO.slug())) {
                throw new DataIntegrityViolationException("Brand with slug '" + brandUpdateDTO.slug() + "' already exists");
            }
        }
        
        // Validate unique constraints if name is being updated
        if (brandUpdateDTO.name() != null && !brandUpdateDTO.name().equals(brand.getName())) {
            if (brandRepository.existsBrandByName(brandUpdateDTO.name())) {
                throw new DataIntegrityViolationException("Brand with name '" + brandUpdateDTO.name() + "' already exists");
            }
        }
        
        brandMapper.updateEntity(brand, brandUpdateDTO);
        Brand updatedBrand = brandRepository.save(brand);
        log.info("Brand updated with id: {}", updatedBrand.getId());
        
        return brandMapper.toDTO(updatedBrand);
    }
    
    @Override
    @CacheEvict(value = {"brands", "brandById", "brandBySlug", "publishedBrands"}, allEntries = true)
    @Transactional
    public void deleteBrand(Long id) {
        log.info("Deleting brand with id: {}", id);
        
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new BrandNotFoundException(id));
        
        brandRepository.delete(brand);
        log.info("Brand deleted with id: {}", id);
    }
}
