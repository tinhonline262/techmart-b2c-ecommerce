package com.shopping.microservices.product_service.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * RedisConfig - Redis & Redisson Configuration
 *
 * KEY CONCEPTS:
 * 1. Redis = Distributed cache (shared across all servers)
 * 2. Redisson = Advanced Redis client with distributed locks
 * 3. TTL = Time To Live (cache lifetime)
 *
 * WHY DIFFERENT TTLs?
 * - Products: 10 min (rarely change)
 * - Categories: 1 hour (very stable)
 * - Search: 5 min (results can change frequently)
 */

@Configuration
public class RedisConfig {

    /**
     * RedisConnectionFactory – Connection to Redis
     * Uses Lettuce (a high-performance, thread-safe Redis client)
     */

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        // config.setPassword("your-redis-password"); // En production

        return new LettuceConnectionFactory(config);
    }

    /**
     * Custom ObjectMapper for Redis
     * Supports Java 8+ date/time types (LocalDateTime, etc.)
     * Enables type information for correct deserialization
     */
    @Bean
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Enable default typing to preserve type information during serialization
        // This ensures objects are deserialized back to their correct types (not LinkedHashMap)
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );

        return mapper;
    }

    /**
     * RedisTemplate – For direct Redis operations
     * Used for custom operations (distributed locks, counters, etc.)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sérialisation with custom ObjectMapper for type info + Java 8 time support
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper());

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * RedissonClient – For distributed locks
     *
     * WHY? To prevent “Cache Stampede”
     * Scenario: 1000 simultaneous requests for a non-cached product
     * Without lock: 1000 database queries
     * With lock: 1 database query, 999 wait for the result
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://localhost:6379")
                .setConnectionPoolSize(50)
                .setConnectionMinimumIdleSize(10)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500);

        return Redisson.create(config);
    }

    /**
     * RedisCacheManager – Manages Redis caches with TTL
     *
     * TTL STRATEGY:
     * - Stable data (categories) = long TTL
     * - Frequent data (products) = medium TTL
     * - Volatile data (search) = short TTL
     */
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {

        // Configuration par défaut - use custom ObjectMapper for type info + Java 8 time support
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer(redisObjectMapper())))
                .disableCachingNullValues();

        // Specific configurations per cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Products: 10 minutes (frequently read, rarely changes)
        cacheConfigurations.put("products",
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        // Product by ID: 15 minutes (very stable)
        cacheConfigurations.put("productById",
                defaultConfig.entryTtl(Duration.ofMinutes(15)));

        // Categories: 1 hour (almost static)
        cacheConfigurations.put("categories",
                defaultConfig.entryTtl(Duration.ofHours(1)));

        // Search results: 5 minutes (can change frequently)
        cacheConfigurations.put("searchResults",
                defaultConfig.entryTtl(Duration.ofMinutes(5)));

        // Price range: 3 minutes (prices fluctuate)
        cacheConfigurations.put("priceRange",
                defaultConfig.entryTtl(Duration.ofMinutes(3)));


        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .enableStatistics()
                .build();
    }

    /**
     * RedissonSpringCacheManager – Alternative using Redisson
     * Used for advanced cases (built-in distributed locks)
     */
    @Bean(name = "redissonCacheManager")
    public CacheManager redissonCacheManager(RedissonClient redissonClient) {
        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<>();

        // Configuration with Redisson
        config.put("distributedCache",
                new org.redisson.spring.cache.CacheConfig(
                        Duration.ofMinutes(10).toMillis(),
                        Duration.ofMinutes(5).toMillis()
                ));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}