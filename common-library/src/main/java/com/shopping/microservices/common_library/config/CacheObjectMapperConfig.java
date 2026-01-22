package com.shopping.microservices.common_library.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common ObjectMapper configurations for cache serialization.
 * 
 * Provides pre-configured ObjectMapper instances for:
 * - Redis cache serialization (with type information)
 * - General REST/JSON operations
 * 
 * Key features:
 * - Java 8+ date/time support (LocalDateTime, etc.)
 * - Type information preservation for correct deserialization
 * - ISO date format instead of timestamps
 */
@Configuration
public class CacheObjectMapperConfig {

    /**
     * ObjectMapper for Redis cache serialization.
     * 
     * Includes type information to ensure objects are deserialized
     * back to their correct types (not LinkedHashMap).
     * 
     * Use this ObjectMapper when configuring:
     * - GenericJackson2JsonRedisSerializer
     * - RedisCacheConfiguration serializers
     * - RedisTemplate value serializers
     */
    @Bean(name = "cacheObjectMapper")
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 date/time module
        mapper.registerModule(new JavaTimeModule());
        
        // Use ISO format for dates instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Enable default typing to preserve type information during serialization
        // This ensures objects are deserialized back to their correct types
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.EVERYTHING,
                JsonTypeInfo.As.PROPERTY
        );
        
        // Don't fail on unknown properties (forward compatibility)
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        return mapper;
    }

    /**
     * ObjectMapper for REST/JSON operations (no type info).
     * 
     * Standard ObjectMapper for REST APIs without type information.
     * Use this for:
     * - REST controller responses
     * - Kafka message serialization
     * - General JSON operations
     */
    @Bean(name = "restObjectMapper")
    public ObjectMapper restObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 date/time module
        mapper.registerModule(new JavaTimeModule());
        
        // Use ISO format for dates instead of timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Don't fail on unknown properties
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        // Don't include null values
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        return mapper;
    }
}
