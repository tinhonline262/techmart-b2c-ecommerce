package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.*;

public record ProductImageCreationDTO(
        @NotNull String imageUrl,
        @NotNull Boolean isPrimary,
        @PositiveOrZero Integer displayOrder
) {}
