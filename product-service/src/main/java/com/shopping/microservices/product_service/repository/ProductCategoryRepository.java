package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    List<ProductCategory> findByProductId(Long productId);

    List<ProductCategory> findByCategoryId(Long categoryId);

    void deleteByProductId(Long productId);

    void deleteByCategoryId(Long categoryId);

    boolean existsByProductIdAndCategoryId(Long productId, Long categoryId);
}
