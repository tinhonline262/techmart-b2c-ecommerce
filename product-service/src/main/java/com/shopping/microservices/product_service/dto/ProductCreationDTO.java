package com.shopping.microservices.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for {@link com.shopping.microservices.product_service.entity.Product}
 */
public record ProductCreationDTO(@NotNull(message = "Name not blank") String name, String description,
                                 @NotNull(message = "SKU not null") String sku,
                                 @NotNull @PositiveOrZero(message = "Price must be positive or zero") BigDecimal price,
                                 @NotNull @PositiveOrZero(message = "Quantity in stock must be positive or zero") Integer quantityInStock,
                                 @NotNull(message = "CategoryID not blank") @Positive(message = "CategoryID must be higher than zero") Long categoryId,
                                 String brand, List<ProductImageCreationDTO> productImages,
                                 @NotNull(message = "isActive not null") Boolean isActive) implements Serializable {

}