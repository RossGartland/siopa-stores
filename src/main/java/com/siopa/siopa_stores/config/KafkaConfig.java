package com.siopa.siopa_stores.config;

import com.siopa.siopa_stores.kafka.OwnerRoleUpdateEvent;
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

/**
 * Kafka configuration for consuming messages related to owner role updates.
 * This class sets up the Kafka consumer factory and listener container.
 */
@Configuration
public class KafkaConfig {

    /**
     * Creates and configures a Kafka consumer factory for handling messages of type {@link OwnerRoleUpdateEvent}.
     * The consumer is set up to deserialize messages from JSON format.
     *
     * @return A configured {@link ConsumerFactory} instance for consuming {@link OwnerRoleUpdateEvent} messages.
     */
    @Bean
    public ConsumerFactory<String, OwnerRoleUpdateEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*"); // Allows deserialization of OwnerRoleUpdateEvent class

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(OwnerRoleUpdateEvent.class));
    }

    /**
     * Creates and configures a Kafka listener container factory for handling messages of type {@link OwnerRoleUpdateEvent}.
     * This factory is used by Kafka listeners to consume and process messages.
     *
     * @return A configured {@link ConcurrentKafkaListenerContainerFactory} for consuming {@link OwnerRoleUpdateEvent} messages.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OwnerRoleUpdateEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OwnerRoleUpdateEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}