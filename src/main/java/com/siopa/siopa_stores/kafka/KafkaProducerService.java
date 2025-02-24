package com.siopa.siopa_stores.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for producing Kafka messages related to owner role updates.
 * Publishes messages to the {@code user-role-updates} topic.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    /** Kafka template for sending messages. */
    private final KafkaTemplate<String, OwnerRoleUpdateEvent> kafkaTemplate;

    /** Kafka topic for user role updates. */
    private static final String TOPIC = "user-role-updates";

    /**
     * Sends a role update event to the Kafka topic.
     *
     * @param event The {@link OwnerRoleUpdateEvent} containing user role update details.
     */
    public void sendRoleUpdateMessage(OwnerRoleUpdateEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}