package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.template.ProductAttributeTemplateDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateCreationDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateDTO;
import com.shopping.microservices.product_service.dto.template.TemplateAttributesAssignmentDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeTemplate;
import com.shopping.microservices.product_service.entity.ProductTemplate;
import com.shopping.microservices.product_service.mapper.ProductTemplateMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import com.shopping.microservices.product_service.repository.ProductAttributeTemplateRepository;
import com.shopping.microservices.product_service.repository.ProductTemplateRepository;
import com.shopping.microservices.product_service.service.ProductTemplateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductTemplateServiceImpl implements ProductTemplateService {

    private final ProductTemplateRepository templateRepository;
    private final ProductAttributeTemplateRepository attributeTemplateRepository;
    private final ProductAttributeRepository attributeRepository;
    private final ProductTemplateMapper templateMapper;

    @Override
    public ProductTemplateDTO createTemplate(ProductTemplateCreationDTO dto) {
        // Check if template with same name already exists
        if (templateRepository.findByName(dto.name()).isPresent()) {
            log.warn("Template with name '{}' already exists", dto.name());
            throw new IllegalArgumentException(
                    "Template with name '" + dto.name() + "' already exists. Template name must be unique.");
        }

        ProductTemplate template = ProductTemplate.builder()
                .name(dto.name())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        ProductTemplate saved = templateRepository.save(template);
        log.info("Template created successfully with id: {}, name: {}", saved.getId(), saved.getName());
        return templateMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductTemplateDTO> getTemplateById(Long id) {
        return templateRepository.findById(id)
                .map(templateMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductTemplateDTO> getAllTemplates() {
        return templateRepository.findAllByOrderByIdAsc()
                .stream()
                .map(templateMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductTemplateDTO updateTemplate(Long id, ProductTemplateCreationDTO dto) {
        ProductTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + id));

        // Check if another template with same name already exists (excluding current
        // template)
        var existingTemplate = templateRepository.findByName(dto.name());
        if (existingTemplate.isPresent() && !existingTemplate.get().getId().equals(id)) {
            log.warn("Template with name '{}' already exists (id: {})", dto.name(), existingTemplate.get().getId());
            throw new IllegalArgumentException(
                    "Template with name '" + dto.name() + "' already exists. Template name must be unique.");
        }

        template.setName(dto.name());
        template.setUpdatedAt(Instant.now());

        ProductTemplate updated = templateRepository.save(template);
        log.info("Template updated successfully with id: {}, name: {}", updated.getId(), updated.getName());
        return templateMapper.toDTO(updated);
    }

    @Override
    public void deleteTemplate(Long id) {
        if (!templateRepository.existsById(id)) {
            throw new EntityNotFoundException("Template not found with id: " + id);
        }
        templateRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductAttributeTemplateDTO> getTemplateAttributes(Long templateId) {
        if (!templateRepository.existsById(templateId)) {
            throw new EntityNotFoundException("Template not found with id: " + templateId);
        }

        return attributeTemplateRepository.findByProductTemplateIdOrderByDisplayOrder(templateId)
                .stream()
                .map(this::mapToAttributeTemplateDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void assignAttributesToTemplate(Long templateId, TemplateAttributesAssignmentDTO dto) {
        ProductTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new EntityNotFoundException("Template not found with id: " + templateId));

        if (dto.attributes() != null && !dto.attributes().isEmpty()) {
            for (TemplateAttributesAssignmentDTO.AttributeAssignmentDTO attrAssignment : dto.attributes()) {
                ProductAttribute attribute = attributeRepository.findById(attrAssignment.attributeId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Attribute not found with id: " + attrAssignment.attributeId()));

                // Check if already exists
                boolean exists = attributeTemplateRepository.existsByTemplateAndAttribute(templateId,
                        attrAssignment.attributeId());
                if (!exists) {
                    ProductAttributeTemplate attributeTemplate = ProductAttributeTemplate.builder()
                            .productTemplate(template)
                            .productAttribute(attribute)
                            .displayOrder(attrAssignment.displayOrder())
                            .build();
                    attributeTemplateRepository.save(attributeTemplate);
                }
            }
        }
    }

    @Override
    public void removeAttributeFromTemplate(Long templateId, Long attributeId) {
        if (!templateRepository.existsById(templateId)) {
            throw new EntityNotFoundException("Template not found with id: " + templateId);
        }
        if (!attributeRepository.existsById(attributeId)) {
            throw new EntityNotFoundException("Attribute not found with id: " + attributeId);
        }
        if (!attributeTemplateRepository.existsByTemplateAndAttribute(templateId, attributeId)) {
            throw new EntityNotFoundException(
                    "Attribute " + attributeId + " is not assigned to template " + templateId);
        }

        attributeTemplateRepository.deleteByProductTemplateIdAndProductAttributeId(templateId, attributeId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateAttributesForTemplate(Long templateId, List<Long> attributeIds) {
        if (attributeIds == null || attributeIds.isEmpty()) {
            return true;
        }

        List<ProductAttributeTemplate> templateAttributes = attributeTemplateRepository
                .findByProductTemplateId(templateId);

        List<Long> allowedAttributeIds = templateAttributes.stream()
                .map(pat -> pat.getProductAttribute().getId())
                .collect(Collectors.toList());

        return allowedAttributeIds.containsAll(attributeIds);
    }

    private ProductAttributeTemplateDTO mapToAttributeTemplateDTO(ProductAttributeTemplate pat) {
        return new ProductAttributeTemplateDTO(
                pat.getProductAttribute().getId(),
                pat.getProductAttribute().getName(),
                pat.getProductAttribute().getProductAttributeGroup() != null
                        ? pat.getProductAttribute().getProductAttributeGroup().getName()
                        : null,
                pat.getDisplayOrder());
    }
}
