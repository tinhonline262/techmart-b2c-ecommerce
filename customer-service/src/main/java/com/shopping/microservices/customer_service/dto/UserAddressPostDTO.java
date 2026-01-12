package com.shopping.microservices.customer_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating user address
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressPostDTO {
    
    @NotNull(message = "Address ID is required")
    private Long addressId;
    
    private Boolean isDefault;
}
