package com.shopping.microservices.common_library.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Utility class for JSON serialization and deserialization.
 * 
 * Provides static methods for converting objects to/from JSON
 * using a pre-configured ObjectMapper with Java 8 date/time support.
 */
public final class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    /**
     * Pre-configured ObjectMapper instance.
     * Thread-safe and reusable.
     */
    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Private constructor to prevent instantiation
    private JsonUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get the shared ObjectMapper instance.
     * 
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * Convert an object to JSON string.
     * 
     * @param obj Object to convert
     * @return JSON string representation
     * @throws JsonProcessingException if serialization fails
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }

    /**
     * Convert an object to JSON string, returning empty string on error.
     * 
     * @param obj Object to convert
     * @return JSON string or empty string on error
     */
    public static String toJsonSafe(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Convert an object to JSON string, returning Optional.
     * 
     * @param obj Object to convert
     * @return Optional containing JSON string or empty if failed
     */
    public static Optional<String> toJsonOptional(Object obj) {
        try {
            return Optional.of(OBJECT_MAPPER.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to JSON: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Convert an object to pretty-printed JSON string.
     * 
     * @param obj Object to convert
     * @return Pretty-printed JSON string
     * @throws JsonProcessingException if serialization fails
     */
    public static String toPrettyJson(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }

    /**
     * Convert an object to byte array.
     * 
     * @param obj Object to convert
     * @return Byte array representation
     * @throws JsonProcessingException if serialization fails
     */
    public static byte[] toBytes(Object obj) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    /**
     * Parse JSON string to object of specified type.
     * 
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     * @throws JsonProcessingException if parsing fails
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, clazz);
    }

    /**
     * Parse JSON string to object of specified type, returning null on error.
     * 
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object or null on error
     */
    public static <T> T fromJsonSafe(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Parse JSON string to object of specified type, returning Optional.
     * 
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Optional containing deserialized object or empty if failed
     */
    public static <T> Optional<T> fromJsonOptional(String json, Class<T> clazz) {
        try {
            return Optional.of(OBJECT_MAPPER.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Parse JSON string to object using TypeReference (for generics).
     * 
     * @param json JSON string
     * @param typeRef TypeReference for target type
     * @param <T> Type parameter
     * @return Deserialized object
     * @throws JsonProcessingException if parsing fails
     */
    public static <T> T fromJson(String json, TypeReference<T> typeRef) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(json, typeRef);
    }

    /**
     * Parse JSON string to object using TypeReference, returning null on error.
     * 
     * @param json JSON string
     * @param typeRef TypeReference for target type
     * @param <T> Type parameter
     * @return Deserialized object or null on error
     */
    public static <T> T fromJsonSafe(String json, TypeReference<T> typeRef) {
        try {
            return OBJECT_MAPPER.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Parse byte array to object of specified type.
     * 
     * @param bytes Byte array
     * @param clazz Target class
     * @param <T> Type parameter
     * @return Deserialized object
     * @throws JsonProcessingException if parsing fails
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws JsonProcessingException {
        try {
            return OBJECT_MAPPER.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new JsonProcessingException("Failed to parse bytes") {
                @Override
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    /**
     * Convert an object to another type (useful for Map to POJO conversion).
     * 
     * @param source Source object
     * @param targetType Target class
     * @param <T> Type parameter
     * @return Converted object
     */
    public static <T> T convertValue(Object source, Class<T> targetType) {
        return OBJECT_MAPPER.convertValue(source, targetType);
    }

    /**
     * Check if a string is valid JSON.
     * 
     * @param json String to check
     * @return true if valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            OBJECT_MAPPER.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}
