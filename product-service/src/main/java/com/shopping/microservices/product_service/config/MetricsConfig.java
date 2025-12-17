package com.shopping.microservices.product_service.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MetricsConfig - Configuration Micrometer pour Prometheus
 *
 * POURQUOI METRICS?
 *
 * Sans metrics:
 * - "Le cache fonctionne" → Intuition, pas de preuve
 * - Pas de visibilité sur performance
 * - Impossible de démontrer ROI
 *
 * Avec metrics:
 * - "95% cache hit ratio" → Preuve objective
 * - "P95 latency: 45ms vs 500ms" → Démontrable
 * - Graphs Grafana → Impressionnant en entretien
 *
 * MÉTRIQUES CRITIQUES:
 * 1. Cache hit/miss ratio
 * 2. Response time (P50, P95, P99)
 * 3. Database query count
 * 4. Throughput (requests/sec)
 * 5. Error rate
 */
@Configuration
public class MetricsConfig {

    /**
     * TimedAspect - Enable @Timed annotation
     *
     * Automatically tracks execution time of methods
     * Usage: @Timed(value = "method.name")
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Custom metrics initialization
     * Called at startup to register custom metrics
     */
    @Bean
    public CustomMetrics customMetrics(MeterRegistry registry) {
        return new CustomMetrics(registry);
    }

    /**
     * CustomMetrics - Pre-register custom metrics
     */
    public static class CustomMetrics {

        private final MeterRegistry registry;

        public CustomMetrics(MeterRegistry registry) {
            this.registry = registry;
            initializeMetrics();
        }

        private void initializeMetrics() {
            // Pre-create timers for consistent metrics
            Timer.builder("product.fetch.time")
                    .description("Time to fetch product")
                    .tag("source", "database")
                    .register(registry);

            Timer.builder("product.fetch.time")
                    .description("Time to fetch product")
                    .tag("source", "cache")
                    .register(registry);

            Timer.builder("cache.operation.time")
                    .description("Time for cache operations")
                    .tag("operation", "get")
                    .register(registry);

            Timer.builder("cache.operation.time")
                    .description("Time for cache operations")
                    .tag("operation", "put")
                    .register(registry);
        }
    }
}