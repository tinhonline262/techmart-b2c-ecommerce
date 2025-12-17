package com.shopping.microservices.order_service.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ReduceStockProductDTO(@NotBlank String sku, @NotNull @Positive Integer quantity) {
}
