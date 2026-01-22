package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByParentId(Long parentId);
    
    List<Category> findByParentIsNull();
    
    boolean existsByName(String name);
    
    // Public category queries
    List<Category> findByIsPublishedTrue();
    
    Optional<Category> findBySlugAndIsPublishedTrue(String slug);
    
    List<Category> findByParentIsNullAndIsPublishedTrue();
    
    List<Category> findByNameContainingIgnoreCaseAndIsPublishedTrue(String keyword);
    
    List<Category> findByParentIdAndIsPublishedTrue(Long parentId);
}
