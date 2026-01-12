package com.shopping.microservices.customer_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user address information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressDTO {
    
    private Long id;
    private String userId;
    private Long addressId;
    private Boolean isDefault;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
