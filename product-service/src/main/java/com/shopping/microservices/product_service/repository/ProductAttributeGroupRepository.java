package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ProductAttributeGroup.
 * Uses ProductOption entity to group related attributes.
 * Groups are differentiated via application logic (e.g., naming conventions).
 */
@Repository
public interface ProductAttributeGroupRepository extends JpaRepository<ProductOption, Long>, JpaSpecificationExecutor<ProductOption> {

    List<ProductOption> findByProductId(Long productId);

    void deleteByProductId(Long productId);

    boolean existsByNameAndProductId(String name, Long productId);
}
