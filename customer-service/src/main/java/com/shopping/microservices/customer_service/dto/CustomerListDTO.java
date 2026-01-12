package com.shopping.microservices.customer_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for listing customers in paginated results
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListDTO {
    
    private Long id;
    private String username;
    private String name;
    private String email;
}
