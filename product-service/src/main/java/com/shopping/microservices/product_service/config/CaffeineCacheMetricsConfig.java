package com.shopping.microservices.product_service.config;

import com.github.benmanes.caffeine.cache.Cache;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Map;

/**
 * Enregistre les métriques Prometheus pour les caches Caffeine existants.
 *
 * Cette classe NE CRÉE PAS de nouveau CacheManager.
 * Elle s'attache aux CacheManagers existants et expose leurs métriques.
 *
 * Métriques exposées :
 * - cache_gets_total{result="hit"}
 * - cache_gets_total{result="miss"}
 * - cache_evictions_total
 * - cache_size
 * - cache_puts_total
 */
@Configuration
public class CaffeineCacheMetricsConfig {

    private static final Logger log = LoggerFactory.getLogger(CaffeineCacheMetricsConfig.class);

    private final MeterRegistry meterRegistry;
    private final ApplicationContext applicationContext;

    public CaffeineCacheMetricsConfig(MeterRegistry meterRegistry, ApplicationContext applicationContext) {
        this.meterRegistry = meterRegistry;
        this.applicationContext = applicationContext;
    }

    /**
     * S'exécute une fois que l'application est prête.
     * Parcourt tous les CacheManagers et enregistre les métriques Caffeine.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void registerCaffeineMetricsToPrometheus() {
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  REGISTERING CAFFEINE CACHE METRICS FOR PROMETHEUS");
        log.info("═══════════════════════════════════════════════════════════");

        // Récupérer tous les CacheManagers du contexte
        Map<String, CacheManager> cacheManagers = applicationContext.getBeansOfType(CacheManager.class);

        log.info("Found {} CacheManager(s): {}", cacheManagers.size(), cacheManagers.keySet());

        int registeredCaches = 0;

        for (Map.Entry<String, CacheManager> entry : cacheManagers.entrySet()) {
            String managerName = entry.getKey();
            CacheManager cacheManager = entry.getValue();

            log.info("Processing CacheManager: {} ({})", managerName, cacheManager.getClass().getSimpleName());

            // Parcourir tous les caches de ce manager
            for (String cacheName : cacheManager.getCacheNames()) {
                org.springframework.cache.Cache springCache = cacheManager.getCache(cacheName);

                if (springCache == null) {
                    log.warn("  Cache '{}' returned null, skipping", cacheName);
                    continue;
                }

                // Vérifier si c'est un cache Caffeine
                if (springCache instanceof CaffeineCache) {
                    CaffeineCache caffeineCache = (CaffeineCache) springCache;
                    Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();

                    // Créer un nom unique pour les métriques
                    String metricCacheName = managerName + "_" + cacheName;

                    try {
                        // Enregistrer les métriques natives Caffeine
                        CaffeineCacheMetrics.monitor(meterRegistry, nativeCache, metricCacheName);
                        log.info("  ✅ Registered metrics for cache: {} -> {}", cacheName, metricCacheName);
                        registeredCaches++;
                    } catch (Exception e) {
                        log.warn("  ⚠️ Failed to register metrics for cache '{}': {}", cacheName, e.getMessage());
                    }
                } else {
                    // Pour les caches non-Caffeine (Redis, etc.), on log juste
                    log.info("  ℹ️ Cache '{}' is not Caffeine ({}), native metrics not available",
                            cacheName, springCache.getClass().getSimpleName());
                }
            }
        }

        log.info("═══════════════════════════════════════════════════════════");
        log.info("  CAFFEINE CACHE METRICS REGISTRATION COMPLETE");
        log.info("  Total Caffeine caches with metrics: {}", registeredCaches);
        log.info("═══════════════════════════════════════════════════════════");
        log.info("  Prometheus Metrics Available:");
        log.info("    - cache_gets_total{result=\"hit\"}");
        log.info("    - cache_gets_total{result=\"miss\"}");
        log.info("    - cache_evictions_total");
        log.info("    - cache_size");
        log.info("    - cache_puts_total");
        log.info("═══════════════════════════════════════════════════════════");
    }
}