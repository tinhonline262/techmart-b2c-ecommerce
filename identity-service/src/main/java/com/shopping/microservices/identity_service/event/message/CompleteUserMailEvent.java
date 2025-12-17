package com.shopping.microservices.identity_service.event.message;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CompleteUserMailEvent {
    private String email;
    private String name;
    private String username;
    private LocalDateTime createdAt;
}
