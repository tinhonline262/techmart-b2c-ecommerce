package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductAttributeGroupRepository
        extends JpaRepository<ProductAttributeGroup, Long>, JpaSpecificationExecutor<ProductAttributeGroup> {

    Optional<ProductAttributeGroup> findByName(String name);
}
