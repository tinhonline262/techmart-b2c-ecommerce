package com.shopping.microservices.common_library.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base Event class for all domain events in the microservices architecture.
 * 
 * Provides common fields for event tracking, correlation, and serialization.
 * All concrete event classes should extend this base class.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class BaseEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier for this event instance.
     * Auto-generated UUID.
     */
    private String eventId;

    /**
     * Type of the event (e.g., ORDER_CREATED, PAYMENT_SUCCESS).
     */
    private String eventType;

    /**
     * Timestamp when the event was created.
     * Auto-generated at construction time.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    /**
     * Source service that generated this event.
     */
    private String source;

    /**
     * Correlation ID for tracking events across services.
     * Used for distributed tracing and debugging.
     */
    private String correlationId;

    /**
     * Constructor that auto-generates eventId and timestamp.
     *
     * @param eventType Type of the event
     * @param source Source service name
     */
    protected BaseEvent(String eventType, String source) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }

    /**
     * Constructor with correlation ID for event chaining.
     *
     * @param eventType Type of the event
     * @param source Source service name
     * @param correlationId Correlation ID for tracking
     */
    protected BaseEvent(String eventType, String source, String correlationId) {
        this(eventType, source);
        this.correlationId = correlationId;
    }
}
