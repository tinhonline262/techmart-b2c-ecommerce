package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.ApiResponse;
import com.shopping.microservices.product_service.dto.template.ProductAttributeTemplateDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateCreationDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateDTO;
import com.shopping.microservices.product_service.dto.template.TemplateAttributesAssignmentDTO;
import com.shopping.microservices.product_service.service.ProductTemplateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin Template Controller
 * Handles CRUD operations for product templates
 * Base path: /api/v1/admin/templates
 */
@RestController
@RequestMapping("/api/v1/admin/templates")
@RequiredArgsConstructor
@Slf4j
public class AdminTemplateController {

    private final ProductTemplateService templateService;

    /**
     * Get all templates
     * 
     * GET /api/v1/admin/templates
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductTemplateDTO>>> getAllTemplates() {
        log.info("Fetching all product templates");
        List<ProductTemplateDTO> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates, "Templates retrieved successfully"));
    }

    /**
     * Get template by ID
     * 
     * GET /api/v1/admin/templates/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductTemplateDTO>> getTemplateById(@PathVariable Long id) {
        log.info("Fetching template with id: {}", id);
        return templateService.getTemplateById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto, "Template retrieved successfully")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Template not found with id: " + id,
                                "/api/v1/admin/templates/" + id)));
    }

    /**
     * Create new template
     * 
     * POST /api/v1/admin/templates
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<ProductTemplateDTO>> createTemplate(
            @Valid @RequestBody ProductTemplateCreationDTO dto) {
        log.info("Creating new template: {}", dto.name());
        try {
            ProductTemplateDTO created = templateService.createTemplate(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(created, "Template created successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Duplicate template name: {}", dto.name());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage(), "/api/v1/admin/templates"));
        }
    }

    /**
     * Update template
     * 
     * PUT /api/v1/admin/templates/{id}
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductTemplateDTO>> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ProductTemplateCreationDTO dto) {
        log.info("Updating template with id: {}", id);
        try {
            ProductTemplateDTO updated = templateService.updateTemplate(id, dto);
            return ResponseEntity.ok(ApiResponse.success(updated, "Template updated successfully"));
        } catch (EntityNotFoundException e) {
            log.warn("Template not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Template not found with id: " + id, "/api/v1/admin/templates/" + id));
        } catch (IllegalArgumentException e) {
            log.warn("Duplicate template name: {}", dto.name());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage(), "/api/v1/admin/templates/" + id));
        }
    }

    /**
     * Delete template
     * 
     * DELETE /api/v1/admin/templates/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTemplate(@PathVariable Long id) {
        log.info("Deleting template with id: {}", id);
        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok(ApiResponse.success("Template deleted successfully"));
        } catch (EntityNotFoundException e) {
            log.warn("Template not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Template not found with id: " + id, "/api/v1/admin/templates/" + id));
        }
    }

    /**
     * Get attributes for a template
     * 
     * GET /api/v1/admin/templates/{id}/attributes
     */
    @GetMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductAttributeTemplateDTO>>> getTemplateAttributes(@PathVariable Long id) {
        log.info("Fetching attributes for template id: {}", id);
        try {
            List<ProductAttributeTemplateDTO> attributes = templateService.getTemplateAttributes(id);
            return ResponseEntity.ok(ApiResponse.success(attributes, "Template attributes retrieved successfully"));
        } catch (EntityNotFoundException e) {
            log.warn("Template not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Template not found with id: " + id,
                            "/api/v1/admin/templates/" + id + "/attributes"));
        }
    }

    /**
     * Assign attributes to template
     * 
     * POST /api/v1/admin/templates/{id}/attributes
     */
    @PostMapping("/{id}/attributes")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<List<ProductAttributeTemplateDTO>>> assignAttributesToTemplate(
            @PathVariable Long id,
            @Valid @RequestBody TemplateAttributesAssignmentDTO dto) {
        log.info("Assigning {} attributes to template id: {}", dto.attributes().size(), id);
        try {
            templateService.assignAttributesToTemplate(id, dto);
            List<ProductAttributeTemplateDTO> attributes = templateService.getTemplateAttributes(id);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(attributes, "Attributes assigned successfully"));
        } catch (EntityNotFoundException e) {
            log.warn("Template not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Template not found with id: " + id,
                            "/api/v1/admin/templates/" + id + "/attributes"));
        }
    }

    /**
     * Remove attribute from template
     * 
     * DELETE /api/v1/admin/templates/{id}/attributes/{attributeId}
     */
    @DeleteMapping("/{id}/attributes/{attributeId}")
    public ResponseEntity<?> removeAttributeFromTemplate(
            @PathVariable Long id,
            @PathVariable Long attributeId) {
        log.info("Removing attribute {} from template {}", attributeId, id);
        try {
            templateService.removeAttributeFromTemplate(id, attributeId);
            return ResponseEntity.ok(ApiResponse.success("Attribute removed successfully"));
        } catch (EntityNotFoundException e) {
            log.warn("Template or attribute not found - Template id: {}, Attribute id: {}", id, attributeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage(),
                            "/api/v1/admin/templates/" + id + "/attributes/" + attributeId));
        }
    }
}
