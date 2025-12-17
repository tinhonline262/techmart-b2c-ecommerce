package com.shopping.microservices.notification_service.event;

import lombok.Builder;
import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record VerifyUserMailEvent(String email, String name, LocalDateTime expiredDate, String verifyToken) implements Serializable {
}