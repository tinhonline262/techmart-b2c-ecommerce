package com.shopping.microservices.notification_service.event;

import lombok.Builder;
import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * DTO for sending complete user mail event
 */
@Builder
public record CompleteUserMailEvent(String email, String name, String username, LocalDateTime createdAt) implements Serializable {
}
