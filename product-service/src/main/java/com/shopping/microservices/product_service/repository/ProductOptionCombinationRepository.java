package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductOptionCombination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionCombinationRepository extends JpaRepository<ProductOptionCombination, Long>, JpaSpecificationExecutor<ProductOptionCombination> {

    List<ProductOptionCombination> findByProductId(Long productId);


    void deleteByProductId(Long productId);
}
