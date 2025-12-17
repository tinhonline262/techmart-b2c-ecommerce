package com.shopping.microservices.identity_service.service;

import com.shopping.microservices.identity_service.entity.UserEntity;

import java.time.Duration;

public interface JwtKeyService {

    String generateToken(UserEntity user, Duration expiry);

    String generateRefreshToken(UserEntity user, Duration expiry);

    boolean validateToken(String token);

    String extractUsername(String token);
}
