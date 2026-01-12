package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, JpaSpecificationExecutor<Brand> {

    Optional<Brand> findBySlug(String slug);

    List<Brand> findByIdIn(List<Long> ids);

    List<Brand> findByIsPublishedTrue();

    boolean existsBySlug(String slug);

    boolean existsByName(String name);
}
