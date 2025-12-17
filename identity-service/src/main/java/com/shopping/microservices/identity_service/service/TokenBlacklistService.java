package com.shopping.microservices.identity_service.service;

public interface TokenBlacklistService {
    void blacklistAccessToken(String token);
    void blacklistRefreshToken(String refreshToken);
    boolean isAccessTokenBlacklisted(String token);
    boolean isRefreshTokenBlacklisted(String token);
}
