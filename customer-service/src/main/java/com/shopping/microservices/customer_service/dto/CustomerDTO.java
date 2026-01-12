package com.shopping.microservices.customer_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for customer information retrieved from identity service
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    
    private Long id;
    private String username;
    private String name;
    private String email;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
