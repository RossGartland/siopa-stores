package com.siopa.siopa_stores.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents an event for updating a user's role in the system.
 * This event is sent to Kafka when a user is assigned a new role as a store owner.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OwnerRoleUpdateEvent {

    /** The unique identifier of the user whose role is being updated. */
    private UUID userId;

    /** The role assigned to the user (e.g., ROLE_STORE_OWNER). */
    private String role;
}