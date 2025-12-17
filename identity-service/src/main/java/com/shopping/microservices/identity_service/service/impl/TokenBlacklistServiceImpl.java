package com.shopping.microservices.identity_service.service.impl;

import com.shopping.microservices.identity_service.config.CustomCacheConfig;
import com.shopping.microservices.identity_service.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final CacheManager cacheManager;

    @Override
    public void blacklistAccessToken(String token) {
        Cache cache = cacheManager.getCache(CustomCacheConfig.CACHE_ACCESS_TOKEN_BLACKLIST);
        if (cache != null) {
            cache.put(token, Boolean.TRUE);
        }
    }

    @Override
    public void blacklistRefreshToken(String refreshToken) {
        Cache cache = cacheManager.getCache(CustomCacheConfig.CACHE_REFRESH_TOKEN_BLACKLIST);
        if (cache != null) {
            cache.put(refreshToken, Boolean.TRUE);
        }
    }

    @Override
    public boolean isAccessTokenBlacklisted(String token) {
        Cache cache = cacheManager.getCache(CustomCacheConfig.CACHE_ACCESS_TOKEN_BLACKLIST);
        if (cache == null) {
            return false;
        }
        return cache.get(token) != null;
    }

    @Override
    public boolean isRefreshTokenBlacklisted(String token) {
        Cache cache = cacheManager.getCache(CustomCacheConfig.CACHE_REFRESH_TOKEN_BLACKLIST);
        if (cache == null) {
            return false;
        }
        return cache.get(token) != null;
    }
}
