package com.shopping.microservices.identity_service.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CustomCacheConfig {

    public static final String CACHE_ACCESS_TOKEN_BLACKLIST = "accessTokenBlacklist";
    public static final String CACHE_REFRESH_TOKEN_BLACKLIST = "refreshTokenBlacklist";

    @Bean
    public CacheManager cacheManager() {

        CaffeineCache accessTokenCache = new CaffeineCache(CACHE_ACCESS_TOKEN_BLACKLIST,
                Caffeine.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES).build());

        CaffeineCache refreshTokenCache = new CaffeineCache(CACHE_REFRESH_TOKEN_BLACKLIST,
                Caffeine.newBuilder().expireAfterWrite(7, TimeUnit.DAYS).build());

        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
               accessTokenCache, refreshTokenCache
        ));
        return manager;
    }
}
