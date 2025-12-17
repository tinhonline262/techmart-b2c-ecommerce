package com.shopping.microservices.notification_service.config;

import com.shopping.microservices.notification_service.event.CompleteUserMailEvent;
import com.shopping.microservices.notification_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.notification_service.event.VerifyUserMailEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    //------------------ Order Placed Event Consumer Configuration ------------------//
    @Bean
    public ConsumerFactory<String, OrderSendNotificationEvent> orderPlacedEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(OrderSendNotificationEvent.class, false));

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderSendNotificationEvent> orderCreatedEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderSendNotificationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderPlacedEventConsumerFactory());
        return factory;
    }

    //------------------ Verify User Mail Event Consumer Configuration ------------------//

    @Bean
    public ConsumerFactory<String, VerifyUserMailEvent> verifyUserMailEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(VerifyUserMailEvent.class, false));

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VerifyUserMailEvent> verifyUserMailEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VerifyUserMailEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(verifyUserMailEventConsumerFactory());
        return factory;
    }

    //------------------ Complete User Mail Event Consumer Configuration ------------------//
    @Bean
    public ConsumerFactory<String, CompleteUserMailEvent> completeUserMailEventConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
                new JsonDeserializer<>(CompleteUserMailEvent.class, false));

    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CompleteUserMailEvent> completeUserMailEventListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CompleteUserMailEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(completeUserMailEventConsumerFactory());
        return factory;
    }

}