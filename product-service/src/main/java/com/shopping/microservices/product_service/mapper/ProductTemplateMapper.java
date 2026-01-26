package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.template.ProductAttributeTemplateDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeTemplate;
import com.shopping.microservices.product_service.entity.ProductTemplate;
import com.shopping.microservices.product_service.repository.ProductAttributeTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductTemplateMapper {
    
    private final ProductAttributeTemplateRepository attributeTemplateRepository;
    
    public ProductTemplateDTO toDTO(ProductTemplate template) {
        if (template == null) return null;
        
        List<ProductAttributeTemplateDTO> attributes = attributeTemplateRepository
                .findByProductTemplateIdOrderByDisplayOrder(template.getId())
                .stream()
                .map(this::mapAttributeTemplate)
                .collect(Collectors.toList());
        
        return new ProductTemplateDTO(
                template.getId(),
                template.getName(),
                attributes,
                template.getCreatedAt(),
                template.getUpdatedAt()
        );
    }
    
    private ProductAttributeTemplateDTO mapAttributeTemplate(ProductAttributeTemplate pat) {
        return new ProductAttributeTemplateDTO(
                pat.getProductAttribute().getId(),
                pat.getProductAttribute().getName(),
                pat.getProductAttribute().getProductAttributeGroup() != null
                    ? pat.getProductAttribute().getProductAttributeGroup().getName()
                    : null,
                pat.getDisplayOrder()
        );
    }
}
