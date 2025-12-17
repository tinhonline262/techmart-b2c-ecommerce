package com.shopping.microservices.product_service.config;


import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
  Two-Level Cache Manager

 Implements TRUE multi-level caching:
 - READ: L1 (Caffeine) → L2 (Redis) → DB
 - WRITE: Both L1 AND L2 simultaneously

 * FLOW:

 * GET:
 * 1. Check L1 (Caffeine) → HIT? Return (ultra-fast)
 * 2. Check L2 (Redis) → HIT? Return + populate L1
 * 3. MISS both → Execute method → Cache in L1 + L2

 * PUT:
 * - Write to L1 (Caffeine) immediately
 * - Write to L2 (Redis) for persistence/sharing

 * EVICT:
 * - Clear from both L1 and L2

 * BENEFITS:
 * - 99% requests served from L1 (Caffeine) - ultra-fast
 * - L2 (Redis) populated for:
 * - Sharing across instances
 * - Surviving L1 evictions
 * - Cold start recovery
 */
public class TwoLevelCacheManager implements CacheManager {

    private final CacheManager l1CacheManager; // Caffeine
    private final CacheManager l2CacheManager; // Redis

    public TwoLevelCacheManager(CacheManager l1CacheManager, CacheManager l2CacheManager) {
        this.l1CacheManager = l1CacheManager;
        this.l2CacheManager = l2CacheManager;
    }

    @Override
    @Nullable
    public Cache getCache(String name) {
        Cache l1Cache = l1CacheManager.getCache(name);
        Cache l2Cache = l2CacheManager.getCache(name);

        if (l1Cache == null && l2Cache == null) {
            return null;
        }

        return new TwoLevelCache(name, l1Cache, l2Cache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return l1CacheManager.getCacheNames();
    }

    /**
     * Two-Level Cache Implementation
     */
    private static class TwoLevelCache implements Cache {

        private final String name;
        private final Cache l1Cache; // Caffeine (can be null)
        private final Cache l2Cache; // Redis (can be null)

        public TwoLevelCache(String name, @Nullable Cache l1Cache, @Nullable Cache l2Cache) {
            this.name = name;
            this.l1Cache = l1Cache;
            this.l2Cache = l2Cache;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Object getNativeCache() {
            return this; // Return composite cache
        }

        @Override
        @Nullable
        public ValueWrapper get(Object key) {
            // 1. Try L1 (Caffeine)
            if (l1Cache != null) {
                ValueWrapper l1Value = l1Cache.get(key);
                if (l1Value != null) {
                    return l1Value; // L1 HIT - ultra-fast
                }
            }

            // 2. Try L2 (Redis)
            if (l2Cache != null) {
                ValueWrapper l2Value = l2Cache.get(key);
                if (l2Value != null) {
                    // L2 HIT - populate L1 for next time
                    if (l1Cache != null && l2Value.get() != null) {
                        l1Cache.put(key, l2Value.get());
                    }
                    return l2Value;
                }
            }

            // 3. MISS both levels
            return null;
        }

        @Override
        @Nullable
        public <T> T get(Object key, @Nullable Class<T> type) {
            ValueWrapper wrapper = get(key);
            return (wrapper != null ? (T) wrapper.get() : null);
        }

        @Override
        @Nullable
        public <T> T get(Object key, Callable<T> valueLoader) {
            // Try to get from cache
            ValueWrapper wrapper = get(key);
            if (wrapper != null) {
                return (T) wrapper.get();
            }

            // Not in cache - load value
            try {
                T value = valueLoader.call();
                put(key, value); // Cache in both levels
                return value;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(Object key, @Nullable Object value) {
            // Write to BOTH levels
            if (l1Cache != null) {
                l1Cache.put(key, value);
            }
            if (l2Cache != null) {
                l2Cache.put(key, value);
            }
        }

        @Override
        @Nullable
        public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
            // Check if exists in L1
            if (l1Cache != null) {
                ValueWrapper existing = l1Cache.get(key);
                if (existing != null) {
                    return existing;
                }
            }

            // Check if exists in L2
            if (l2Cache != null) {
                ValueWrapper existing = l2Cache.get(key);
                if (existing != null) {
                    // Populate L1
                    if (l1Cache != null && existing.get() != null) {
                        l1Cache.put(key, existing.get());
                    }
                    return existing;
                }
            }

            // Doesn't exist - put in both
            put(key, value);
            return null;
        }

        @Override
        public void evict(Object key) {
            // Evict from BOTH levels
            if (l1Cache != null) {
                l1Cache.evict(key);
            }
            if (l2Cache != null) {
                l2Cache.evict(key);
            }
        }

        @Override
        public boolean evictIfPresent(Object key) {
            boolean l1Evicted = l1Cache != null && l1Cache.evictIfPresent(key);
            boolean l2Evicted = l2Cache != null && l2Cache.evictIfPresent(key);
            return l1Evicted || l2Evicted;
        }

        @Override
        public void clear() {
            // Clear BOTH levels
            if (l1Cache != null) {
                l1Cache.clear();
            }
            if (l2Cache != null) {
                l2Cache.clear();
            }
        }

        @Override
        public boolean invalidate() {
            boolean l1Invalidated = l1Cache != null && l1Cache.invalidate();
            boolean l2Invalidated = l2Cache != null && l2Cache.invalidate();
            return l1Invalidated || l2Invalidated;
        }
    }
}