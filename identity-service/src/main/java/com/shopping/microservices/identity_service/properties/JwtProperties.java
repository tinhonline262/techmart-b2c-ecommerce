package com.shopping.microservices.identity_service.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@ConfigurationProperties(prefix = "identity.jwt")
@Component
@Data
public class JwtProperties {
    private Duration tokenExp;
    private Duration refreshTokenExp;
    private String secretKey;
}
