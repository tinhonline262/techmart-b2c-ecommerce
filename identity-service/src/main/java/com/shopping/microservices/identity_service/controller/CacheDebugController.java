package com.shopping.microservices.identity_service.controller;

import com.shopping.microservices.identity_service.constant.ProfileConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/debug/cache")
@RequiredArgsConstructor
@Profile(ProfileConstant.DEVELOPMENT)
public class CacheDebugController {

    private final CacheManager cacheManager;

    @GetMapping("/{cacheName}")
    public Map<Object, Object> getCacheContent(@PathVariable String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache instanceof CaffeineCache caffeineCache) {
            return caffeineCache.getNativeCache().asMap();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cache not found or not Caffeine");
    }
}
