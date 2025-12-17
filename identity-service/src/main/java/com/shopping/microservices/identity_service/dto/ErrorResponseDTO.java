package com.shopping.microservices.identity_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponseDTO {
    private int status;
    private String message;
    private String path;
}
