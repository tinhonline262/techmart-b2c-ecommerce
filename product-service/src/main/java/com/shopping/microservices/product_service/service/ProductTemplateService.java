package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.template.ProductAttributeTemplateDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateCreationDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateDTO;
import com.shopping.microservices.product_service.dto.template.TemplateAttributesAssignmentDTO;
import com.shopping.microservices.product_service.entity.ProductTemplate;

import java.util.List;
import java.util.Optional;

public interface ProductTemplateService {

    /**
     * Create a new product template
     */
    ProductTemplateDTO createTemplate(ProductTemplateCreationDTO dto);

    /**
     * Get template by ID
     */
    Optional<ProductTemplateDTO> getTemplateById(Long id);

    /**
     * Get all templates
     */
    List<ProductTemplateDTO> getAllTemplates();

    /**
     * Update template
     */
    ProductTemplateDTO updateTemplate(Long id, ProductTemplateCreationDTO dto);

    /**
     * Delete template
     */
    void deleteTemplate(Long id);

    /**
     * Get attributes for a specific template
     */
    List<ProductAttributeTemplateDTO> getTemplateAttributes(Long templateId);

    /**
     * Assign attributes to a template
     */
    void assignAttributesToTemplate(Long templateId, TemplateAttributesAssignmentDTO dto);

    /**
     * Remove an attribute from template
     */
    void removeAttributeFromTemplate(Long templateId, Long attributeId);

    /**
     * Validate if all provided attributes belong to a template
     */
    boolean validateAttributesForTemplate(Long templateId, List<Long> attributeIds);
}
