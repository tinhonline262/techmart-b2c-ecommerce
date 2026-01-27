package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplate, Long> {
    Optional<ProductTemplate> findByName(String name);

    List<ProductTemplate> findAllByOrderByNameAsc();

    List<ProductTemplate> findAllByOrderByIdAsc();
}
