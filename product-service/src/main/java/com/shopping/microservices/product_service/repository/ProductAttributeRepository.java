package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long>, JpaSpecificationExecutor<ProductAttribute> {
    List<ProductAttribute> findByProductAttributeGroupId(Long groupId);
    boolean existsByName(String name);
    Optional<ProductAttribute> findByNameIgnoreCase(String name);

}

