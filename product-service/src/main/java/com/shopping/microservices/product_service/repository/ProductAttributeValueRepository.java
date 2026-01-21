package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long>, JpaSpecificationExecutor<ProductAttributeValue> {
    List<ProductAttributeValue> findByProductAttributeId(Long attributeId);
    List<ProductAttributeValue> findByProductAttributeProductId(Long productId);
    void deleteByProductAttributeId(Long attributeId);
    void deleteByProductAttributeProductId(Long productId);
}
