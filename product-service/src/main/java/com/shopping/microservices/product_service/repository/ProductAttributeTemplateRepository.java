package com.shopping.microservices.product_service.repository;

import com.shopping.microservices.product_service.entity.ProductAttributeTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeTemplateRepository extends JpaRepository<ProductAttributeTemplate, Long> {

    List<ProductAttributeTemplate> findByProductTemplateIdOrderByDisplayOrder(Long templateId);

    List<ProductAttributeTemplate> findByProductTemplateId(Long templateId);

    @Query("SELECT COUNT(pat) > 0 FROM ProductAttributeTemplate pat " +
            "WHERE pat.productTemplate.id = :templateId AND pat.productAttribute.id = :attributeId")
    boolean existsByTemplateAndAttribute(@Param("templateId") Long templateId, @Param("attributeId") Long attributeId);

    void deleteByProductTemplateIdAndProductAttributeId(Long templateId, Long attributeId);
}
