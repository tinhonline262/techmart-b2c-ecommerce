package com.shopping.microservices.notification_service.config;

import com.shopping.microservices.notification_service.event.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseProps(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }
    //------------------ Verify User Mail Event Consumer Configuration ------------------//

    @Bean
    public ConsumerFactory<String, VerifyUserMailEvent> verifyUserMailEventConsumerFactory() {

        return new DefaultKafkaConsumerFactory<>(baseProps("notification-service-verify-mail-group"), new StringDeserializer(),
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
        return new DefaultKafkaConsumerFactory<>(baseProps("notification-service-completed-user-group"), new StringDeserializer(),
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