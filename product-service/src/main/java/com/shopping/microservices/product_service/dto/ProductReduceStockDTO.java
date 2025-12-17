package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductReduceStockDTO(@NotBlank String sku, @NotNull @Positive Integer quantity) {
}
