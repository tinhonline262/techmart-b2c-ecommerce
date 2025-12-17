package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage,Long> {
}
