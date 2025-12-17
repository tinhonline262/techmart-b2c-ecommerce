package com.shopping.microservices.identity_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDTO {
    private String username;
    private String email;
    private String name;
    private String oauthId;
    private List<String> roles;
    private String token;
    private String refreshToken;
}
