package com.shopping.microservices.product_service.dto.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record ProductTemplateCreationDTO(
        @NotBlank(message = "Template name is required") @Size(max = 255, message = "Template name must not exceed 255 characters") String name)
        implements Serializable {
}
