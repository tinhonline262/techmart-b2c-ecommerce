package com.shopping.microservices.identity_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDTO {
    private String id;
    private String username;
    private String email;
    private String name;
    private List<String> roles;
    private LocalDateTime createdAt;
}
