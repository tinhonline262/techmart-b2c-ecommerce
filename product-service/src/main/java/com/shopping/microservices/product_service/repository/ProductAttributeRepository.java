package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long>, JpaSpecificationExecutor<ProductAttribute> {

    List<ProductAttribute> findByProductId(Long productId);

    void deleteByProductId(Long productId);

    boolean existsByNameAndProductId(String name, Long productId);
}
