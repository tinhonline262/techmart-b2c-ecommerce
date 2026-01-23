package com.shopping.microservices.common_library.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shopping.microservices.common_library.kafka.BaseEvent;
import lombok.*;

import java.util.Map;

/**
 * Notification Event for publishing notification-related domain events.
 * 
 * Used for triggering email, SMS, and push notifications from 
 * the Notification Service.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationEvent extends BaseEvent {

    private static final long serialVersionUID = 1L;

    /**
     * Notification event types (delivery channels)
     */
    public enum NotificationEventType {
        EMAIL_SEND,
        SMS_SEND,
        PUSH_NOTIFICATION
    }

    /**
     * Notification templates
     */
    public enum NotificationTemplate {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        ORDER_SHIPPED,
        ORDER_DELIVERED,
        PAYMENT_SUCCESS,
        PAYMENT_FAILED,
        PAYMENT_REFUNDED,
        INVENTORY_LOW_STOCK,
        SHIPMENT_UPDATE,
        WELCOME,
        PASSWORD_RESET,
        ACCOUNT_VERIFICATION
    }

    /**
     * Notification priority levels
     */
    public enum NotificationPriority {
        HIGH,
        MEDIUM,
        LOW
    }

    /**
     * Recipient (email address or phone number)
     */
    private String recipient;

    /**
     * Template to use for notification content
     */
    private NotificationTemplate template;

    /**
     * Subject line (for email notifications)
     */
    private String subject;

    /**
     * Template variables for dynamic content
     */
    private Map<String, Object> data;

    /**
     * Priority level for notification processing
     */
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.MEDIUM;

    /**
     * Customer name for personalization
     */
    private String customerName;

    /**
     * Language/Locale for the notification
     */
    @Builder.Default
    private String locale = "vi-VN";

    /**
     * Constructor with event type and source
     */
    public NotificationEvent(NotificationEventType eventType, String source) {
        super(eventType.name(), source);
    }

    /**
     * Constructor with event type, source, and correlation ID
     */
    public NotificationEvent(NotificationEventType eventType, String source, String correlationId) {
        super(eventType.name(), source, correlationId);
    }

    /**
     * Static factory method for creating email notification events
     */
    public static NotificationEvent email(String source, String recipient, 
                                          NotificationTemplate template, 
                                          String subject, Map<String, Object> data) {
        NotificationEvent event = new NotificationEvent(NotificationEventType.EMAIL_SEND, source);
        event.setRecipient(recipient);
        event.setTemplate(template);
        event.setSubject(subject);
        event.setData(data);
        return event;
    }

    /**
     * Static factory method for creating SMS notification events
     */
    public static NotificationEvent sms(String source, String phoneNumber, 
                                        NotificationTemplate template, 
                                        Map<String, Object> data) {
        NotificationEvent event = new NotificationEvent(NotificationEventType.SMS_SEND, source);
        event.setRecipient(phoneNumber);
        event.setTemplate(template);
        event.setData(data);
        return event;
    }

    /**
     * Static factory method for creating push notification events
     */
    public static NotificationEvent pushNotification(String source, String deviceToken, 
                                                     NotificationTemplate template, 
                                                     String subject, Map<String, Object> data) {
        NotificationEvent event = new NotificationEvent(NotificationEventType.PUSH_NOTIFICATION, source);
        event.setRecipient(deviceToken);
        event.setTemplate(template);
        event.setSubject(subject);
        event.setData(data);
        return event;
    }

    /**
     * Static factory method for high-priority notifications
     */
    public static NotificationEvent highPriority(String source, NotificationEventType type, 
                                                  String recipient, NotificationTemplate template, 
                                                  Map<String, Object> data) {
        NotificationEvent event = new NotificationEvent(type, source);
        event.setRecipient(recipient);
        event.setTemplate(template);
        event.setData(data);
        event.setPriority(NotificationPriority.HIGH);
        return event;
    }
}
