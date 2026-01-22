package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.category.CategoryCreationDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDTO;
import com.shopping.microservices.product_service.dto.category.CategoryDetailDTO;
import com.shopping.microservices.product_service.dto.category.CategorySuggestionDTO;
import com.shopping.microservices.product_service.dto.category.CategoryUpdateDTO;

import java.util.List;
import java.util.stream.Collectors;
import com.shopping.microservices.product_service.entity.Category;
import com.shopping.microservices.product_service.exception.CategoryNotFoundException;
import com.shopping.microservices.product_service.mapper.CategoryMapper;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import com.shopping.microservices.product_service.repository.ProductCategoryRepository;
import com.shopping.microservices.product_service.service.CategoryService;
import com.shopping.microservices.product_service.util.ProductConverter;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    @Cacheable(value = "categories_page", key = "{#keyword, #pageable.pageNumber, #pageable.pageSize}")
    @Transactional(readOnly = true)
    public PageResponseDTO<CategoryDTO> getCategories(String keyword, Pageable pageable) {
        log.info("Fetching categories with keyword: {}, page: {}", keyword, pageable.getPageNumber());
        
        Page<Category> categoryPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        
        Page<CategoryDTO> dtoPage = categoryPage.map(CategoryDTO::toDTO);
        
        log.info("Retrieved {} categories out of {} total", dtoPage.getNumberOfElements(), dtoPage.getTotalElements());
        
        return new PageResponseDTO<>(
                dtoPage.getContent(),
                dtoPage.getNumber(),
                dtoPage.getSize(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.isLast(),
                dtoPage.isEmpty()
        );
    }

    @Override
    @Cacheable(value = "category_by_id", key = "#id")
    @Transactional(readOnly = true)
    public CategoryDetailDTO getCategoryById(Long id) {
        log.info("Fetching category with id: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        CategoryDetailDTO detailDTO = categoryMapper.toDetailDTO(category);
        
        log.info("Successfully retrieved category: {}", category.getName());
        
        return detailDTO;
    }

    @Override
    @CacheEvict(value = {"categories_page", "category_by_id", "published_categories", "category_by_slug", "category_suggestions"}, allEntries = true)
    @Transactional
    public CategoryDTO createCategory(CategoryCreationDTO categoryCreationDTO) {
        validateCategoryExisted(categoryCreationDTO.name());
        log.info("Creating new category: {}", categoryCreationDTO.name());
        
        // Generate slug if not provided
        String slug = categoryCreationDTO.slug();
        if (slug == null || slug.trim().isEmpty()) {
            slug = ProductConverter.toSlug(categoryCreationDTO.name());
            log.debug("Generated slug: {} from name: {}", slug, categoryCreationDTO.name());
        }
        
        // Validate parent category exists if parentId is provided
        if (categoryCreationDTO.parentId() != null) {
            categoryRepository.findById(categoryCreationDTO.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryCreationDTO.parentId()));
        }
        
        // Create CategoryCreationDTO with generated slug
        CategoryCreationDTO dtoWithSlug = new CategoryCreationDTO(
                categoryCreationDTO.name(),
                slug,
                categoryCreationDTO.description(),
                categoryCreationDTO.imageUrl(),
                categoryCreationDTO.parentId(),
                categoryCreationDTO.displayOrder(),
                categoryCreationDTO.isPublished(),
                categoryCreationDTO.metaTitle(),
                categoryCreationDTO.metaDescription(),
                categoryCreationDTO.metaKeywords()
        );
        
        Category category = categoryMapper.toEntity(dtoWithSlug);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Successfully created category with id: {}", savedCategory.getId());
        
        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @CacheEvict(value = {"categories_page", "category_by_id", "published_categories", "category_by_slug", "category_suggestions"}, allEntries = true)
    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryUpdateDTO categoryUpdateDTO) {
        validateCategoryExisted(categoryUpdateDTO.name());

        log.info("Updating category with id: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        // Validate parent category exists if parentId is being updated
        if (categoryUpdateDTO.parentId() != null) {
            // Prevent circular reference: category cannot be its own parent
            if (categoryUpdateDTO.parentId().equals(id)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            categoryRepository.findById(categoryUpdateDTO.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(categoryUpdateDTO.parentId()));
        }
        
        // Generate slug if name is being updated but slug is not provided
        if (categoryUpdateDTO.name() != null && categoryUpdateDTO.slug() == null) {
            String generatedSlug = ProductConverter.toSlug(categoryUpdateDTO.name());
            CategoryUpdateDTO dtoWithSlug = new CategoryUpdateDTO(
                    categoryUpdateDTO.name(),
                    generatedSlug,
                    categoryUpdateDTO.description(),
                    categoryUpdateDTO.imageUrl(),
                    categoryUpdateDTO.parentId(),
                    categoryUpdateDTO.displayOrder(),
                    categoryUpdateDTO.isPublished(),
                    categoryUpdateDTO.metaTitle(),
                    categoryUpdateDTO.metaDescription(),
                    categoryUpdateDTO.metaKeywords()
            );
            categoryMapper.updateEntity(category, dtoWithSlug);
        } else {
            categoryMapper.updateEntity(category, categoryUpdateDTO);
        }
        
        Category updatedCategory = categoryRepository.save(category);
        
        log.info("Successfully updated category: {}", updatedCategory.getName());
        
        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    @CacheEvict(value = {"categories_page", "category_by_id", "published_categories", "category_by_slug", "category_suggestions"}, allEntries = true)
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Deleting category with id: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        
        // Check if category has products
        int productCount = productCategoryRepository.findByCategoryId(id).size();
        if (productCount > 0) {
            throw new IllegalStateException(
                    String.format("Cannot delete category '%s' because it has %d associated products", 
                            category.getName(), productCount));
        }
        
        // Check if category has child categories
        if (!categoryRepository.findByParentId(id).isEmpty()) {
            throw new IllegalStateException(
                    String.format("Cannot delete category '%s' because it has child categories", 
                            category.getName()));
        }
        
        categoryRepository.delete(category);
        
        log.info("Successfully deleted category: {}", category.getName());
    }

    private void validateCategoryExisted(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new IllegalArgumentException("Category with name '" + name + "' already exists.");
        }
    }

    // Public Category Methods

    @Override
    @Cacheable(value = "published_categories", key = "{#parentId, #includeChildren}")
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllPublishedCategories(Long parentId, Boolean includeChildren) {
        log.info("Fetching published categories with parentId: {}, includeChildren: {}", parentId, includeChildren);
        
        List<Category> categories;
        
        if (parentId != null) {
            // Get children of specific parent
            categories = categoryRepository.findByParentIdAndIsPublishedTrue(parentId);
        } else if (includeChildren != null && includeChildren) {
            // Get all published categories (flat list)
            categories = categoryRepository.findByIsPublishedTrue();
        } else {
            // Get only root categories (no parent)
            categories = categoryRepository.findByParentIsNullAndIsPublishedTrue();
        }
        
        List<CategoryDTO> categoryDTOs = categoryMapper.toDTOList(categories);
        
        log.info("Retrieved {} published categories", categoryDTOs.size());
        
        return categoryDTOs;
    }

    @Override
    @Cacheable(value = "category_by_slug", key = "#slug")
    @Transactional(readOnly = true)
    public CategoryDetailDTO getCategoryBySlug(String slug) {
        log.info("Fetching published category by slug: {}", slug);
        
        Category category = categoryRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with slug: " + slug));
        
        CategoryDetailDTO detailDTO = categoryMapper.toDetailDTO(category);
        
        log.info("Successfully retrieved category: {}", category.getName());
        
        return detailDTO;
    }

    @Override
    @Cacheable(value = "category_suggestions", key = "{#keyword, #limit}")
    @Transactional(readOnly = true)
    public List<CategorySuggestionDTO> getCategorySuggestions(String keyword, int limit) {
        log.info("Fetching category suggestions with keyword: {}, limit: {}", keyword, limit);
        
        List<Category> categories;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search by keyword in published categories
            categories = categoryRepository.findByNameContainingIgnoreCaseAndIsPublishedTrue(keyword.trim());
        } else {
            // Get all published root categories
            categories = categoryRepository.findByParentIsNullAndIsPublishedTrue();
        }
        
        // Apply limit
        if (limit > 0 && categories.size() > limit) {
            categories = categories.subList(0, limit);
        }
        
        // Build hierarchical structure with children
        List<CategorySuggestionDTO> suggestions = categories.stream()
                .map(this::buildCategorySuggestionWithChildren)
                .collect(Collectors.toList());
        
        log.info("Retrieved {} category suggestions", suggestions.size());
        
        return suggestions;
    }

    /**
     * Recursively build CategorySuggestionDTO with children
     */
    private CategorySuggestionDTO buildCategorySuggestionWithChildren(Category category) {
        // Get product count for this category
        int productCount = productCategoryRepository.findByCategoryId(category.getId()).size();
        
        // Get published children
        List<Category> children = categoryRepository.findByParentIdAndIsPublishedTrue(category.getId());
        
        // Recursively build children suggestions
        List<CategorySuggestionDTO> childSuggestions = children.stream()
                .map(this::buildCategorySuggestionWithChildren)
                .collect(Collectors.toList());
        
        return new CategorySuggestionDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                null, // imageUrl - placeholder for future image service
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getName() : null,
                childSuggestions,
                productCount
        );
    }
}
