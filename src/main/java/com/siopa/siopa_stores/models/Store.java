package com.siopa.siopa_stores.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Represents a store entity in the system.
 */
@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    /**
     * Unique identifier for the store.
     */
    @Id
    @UuidGenerator
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;

    /**
     * Name of the store (mandatory, max 100 characters).
     */
    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Region where the store is located (max 50 characters).
     */
    @Size(max = 50)
    @Column(length = 50)
    private String region;

    /**
     * Address of the store (mandatory, max 200 characters).
     */
    @NotBlank(message = "Address is mandatory")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String address;

    /**
     * Indicates whether the store is active.
     */
    @Column(nullable = false)
    private boolean isActive = true;

    /**
     * Phone number of the store (max 15 characters).
     */
    @Size(max = 15)
    @Column(length = 15)
    private String phoneNumber;

    /**
     * Email address of the store (mandatory, must be unique and valid).
     */
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * List of owner IDs associated with the store.
     */
    @ElementCollection
    @CollectionTable(name = "store_owners", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "owner_id")
    private List<UUID> ownerIds;

    /**
     * Latitude coordinate of the store (mandatory).
     */
    @NotNull
    @Column(name="latitude")
    private double latitude;

    /**
     * Longitude coordinate of the store (mandatory).
     */
    @NotNull
    @Column(name="longitude")
    private double longitude;

    /**
     * Type of store (e.g., grocery, electronics, etc.).
     */
    @Column(name="store_type")
    private String storeType;

    /**
     * Rating of the store (integer value).
     */
    @Column(name="rating")
    private int rating;

    /**
     * Delivery fee charged by the store.
     */
    @Column(name="delivery_fee")
    private BigDecimal deliveryFee;
}
