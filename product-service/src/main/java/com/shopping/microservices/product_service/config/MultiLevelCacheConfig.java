package com.shopping.microservices.product_service.config;


import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Multi-Level Cache Manager Configuration
 *
 * ARCHITECTURE:
 * Request → Caffeine (L1, in-memory) → Redis (L2, distributed) → Database
 * ~1ms ~10ms ~300ms
 *
 * FLOW:
 *
 * GET (Read):
 * 1. Check Caffeine (L1) → HIT? Return immediately (1ms)
 * 2. Check Redis (L2) → HIT? Return + populate L1 (10ms)
 * 3. MISS both → Query DB → Cache in L1 + L2 (300ms)
 *
 * PUT (Write):
 * - Write to L1 (Caffeine) for speed
 * - Write to L2 (Redis) for persistence/sharing
 *
 * EVICT (Clear):
 * - Clear from both L1 and L2
 *
 * BENEFITS:
 * - 99% requests served from L1 (Caffeine) → 1ms latency
 * - L2 (Redis) acts as:
 * - Backup when L1 evicts entries
 * - Shared cache across instances
 * - Cold start recovery
 * - Reduces DB queries by 99.9%
 *
 * TRADE-OFFS:
 * - Slightly higher write latency (writes to 2 caches)
 * - Memory used in both L1 and L2
 * - Must invalidate both levels on updates
 */
@Configuration
public class MultiLevelCacheConfig {

    /**
     * Two-Level Cache Manager
     *
     * Unlike CompositeCacheManager (read-only fallback),
     * TwoLevelCacheManager writes to BOTH levels:
     *
     * - L1 (Caffeine): Ultra-fast, local
     * - L2 (Redis): Shared, persistent
     *
     * This ensures Redis is populated for:
     * 1. Other app instances to use
     * 2. Recovery after L1 eviction
     * 3. Cold start scenarios
     *
     * @Primary: Makes this the default CacheManager
     */
    @Bean
    @Primary
    public CacheManager twoLevelCacheManager(
            CacheManager caffeineCacheManager,
            CacheManager redisCacheManager) {

        return new TwoLevelCacheManager(
                caffeineCacheManager, // L1 - Read first, write first
                redisCacheManager // L2 - Read fallback, write always
        );
    }
}