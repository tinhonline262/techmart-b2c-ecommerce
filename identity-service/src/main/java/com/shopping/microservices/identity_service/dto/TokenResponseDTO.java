package com.shopping.microservices.identity_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}
