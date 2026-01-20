package com.shopping.microservices.cart_service.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * CaffeineConfig - Local cache configuration (L1)
 *
 * 🎯 CONCEPT: Multi-Level Caching
 *
 * L1 (Caffeine) → L2 (Redis) → Database
 *    1-5ms         10-20ms       300-500ms
 **/
@Configuration
@EnableCaching
public class CaffeineConfig {

    /**
     * CacheManager for Caffeine (L1 cache)
     *
     * EXPLAINED PARAMETERS:
     *
     * maximumSize(10_000):
     * - Limits the cache to 10K entries
     * - Prevents OutOfMemoryError
     * - Uses LRU (Least Recently Used) eviction
     *
     * expireAfterWrite(5 minutes):
     * - Short TTL for freshness
     * - Shorter than Redis (conservative strategy)
     *
     * expireAfterAccess(3 minutes):
     * - Resets TTL when an entry is accessed
     * - Keeps hot data longer
     *
     * recordStats():
     * - Tracks hit/miss ratio
     * - Essential for monitoring
     **/

    @Bean(name = "caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                "customerCart"    // Customer cart items cache
        );

        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Max size (memory protection)
                .maximumSize(10_000)

                // Absolute TTL
                .expireAfterWrite(5, TimeUnit.MINUTES)

                // Relative TTL (resets on access)
                .expireAfterAccess(3, TimeUnit.MINUTES)

                // Metrics (important for monitoring)
                .recordStats()

                // Soft values = evicted under GC pressure
                .softValues()
        );

        return cacheManager;
    }

    /**
     * Configuration for ultra-stable data
     * e.g., system configuration, lookup tables
     */
    @Bean(name = "longTermCaffeineCache")
    public CacheManager longTermCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("longTermCache");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(1, TimeUnit.HOURS) // Long TTL
                .recordStats()
        );

        return cacheManager;
    }
    /**
     * Configuration for volatile data
     * e.g., search results, temporary data
     */
    @Bean(name = "shortTermCaffeineCache")
    public CacheManager shortTermCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("searchCache");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(5_000)
                .expireAfterWrite(1, TimeUnit.MINUTES) // Very short TTL
                .recordStats()
        );

        return cacheManager;
    }
}