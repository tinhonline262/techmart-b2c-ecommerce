package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.Brand;
import com.shopping.microservices.product_service.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {
    Page<Brand> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Brand> findBySlug(String slug);

    List<Brand> findByIdIn(List<Long> ids);

    Page<Brand> findByIsPublishedTrue(Pageable pageable);
    List<Brand> findByIsPublishedTrue();
    Brand findBrandsBySlugIsAndIsPublishedTrue(String slug);

    boolean existsBySlug(String slug);
    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
    boolean existsBrandByName(String name);
}
