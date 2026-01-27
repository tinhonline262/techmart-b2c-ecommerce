package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.ApiResponse;
import com.shopping.microservices.product_service.dto.template.ProductAttributeTemplateDTO;
import com.shopping.microservices.product_service.dto.template.ProductTemplateDTO;
import com.shopping.microservices.product_service.service.ProductTemplateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public Template Controller
 * Handles read-only operations for product templates for customers
 * Base path: /api/v1/public/templates
 */
@RestController
@RequestMapping("/api/v1/public/templates")
@RequiredArgsConstructor
@Slf4j
public class PublicTemplateController {

    private final ProductTemplateService templateService;

    /**
     * Get all templates
     * 
     * GET /api/v1/public/templates
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<List<ProductTemplateDTO>>> getAllTemplates() {
        log.info("Fetching all product templates");
        List<ProductTemplateDTO> templates = templateService.getAllTemplates();
        return ResponseEntity.ok(ApiResponse.success(templates, "Templates retrieved successfully"));
    }

    /**
     * Get template by ID with its attributes
     * 
     * GET /api/v1/public/templates/{id}
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<ProductTemplateDTO>> getTemplateById(@PathVariable Long id) {
        log.info("Fetching template with id: {}", id);
        return templateService.getTemplateById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.success(dto, "Template retrieved successfully")))
                .orElseGet(() -> {
                    log.warn("Template not found with id: {}", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.error(404, "Template not found with id: " + id,
                                    "/api/v1/public/templates/" + id));
                });
    }

    /**
     * Get attributes for a template
     * 
     * GET /api/v1/public/templates/{id}/attributes
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
                            "/api/v1/public/templates/" + id + "/attributes"));
        }
    }
}
