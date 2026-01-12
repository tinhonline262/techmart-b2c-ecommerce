package com.shopping.microservices.customer_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Extended DTO for admin view of customer information
 * Includes additional administrative fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAdminDTO {
    
    private Long id;
    private String username;
    private String name;
    private String email;
    private Boolean isActive;
    private String role;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastLoginAt;
}
