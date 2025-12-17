package com.shopping.microservices.identity_service.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDTO(@NotBlank String refreshToken) {
}
