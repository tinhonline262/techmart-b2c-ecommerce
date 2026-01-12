package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long>, JpaSpecificationExecutor<ProductOptionValue> {

    List<ProductOptionValue> findByProductOptionId(Long optionId);

    List<ProductOptionValue> findByProductOptionProductId(Long productId);

    void deleteByProductOptionId(Long optionId);

    void deleteByProductOptionProductId(Long productId);
}
