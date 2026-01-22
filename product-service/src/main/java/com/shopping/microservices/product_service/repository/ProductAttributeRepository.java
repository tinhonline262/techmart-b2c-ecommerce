package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductAttributeRepository
        extends JpaRepository<ProductAttribute, Long>, JpaSpecificationExecutor<ProductAttribute> {

    Page<ProductAttribute> findAll(Pageable pageable);

    List<ProductAttribute> findByProductAttributeGroupId(Long groupId);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    Optional<ProductAttribute> findByName(String name);

    List<ProductAttribute> findByIdIn(List<Long> ids);
}
