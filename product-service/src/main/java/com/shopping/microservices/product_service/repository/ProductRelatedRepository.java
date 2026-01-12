package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductRelated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRelatedRepository extends JpaRepository<ProductRelated, Long> {

    List<ProductRelated> findByProductId(Long productId);

    List<ProductRelated> findByRelatedProductId(Long relatedProductId);

    void deleteByProductId(Long productId);

    void deleteByRelatedProductId(Long relatedProductId);

    boolean existsByProductIdAndRelatedProductId(Long productId, Long relatedProductId);
}
