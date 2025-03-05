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

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @UuidGenerator
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;

    @NotBlank(message = "Name is mandatory")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 50)
    @Column(length = 50)
    private String region;

    @NotBlank(message = "Address is mandatory")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String address;

    @Column(nullable = false)
    private boolean isActive = true;

    @Size(max = 15)
    @Column(length = 15)
    private String phoneNumber;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection
    @CollectionTable(name = "store_owners", joinColumns = @JoinColumn(name = "store_id"))
    @Column(name = "owner_id")
    private List<UUID> ownerIds;

    @NotNull
    @Column(name="latitude")
    private double latitude;

    @NotNull
    @Column(name="longitude")
    private double longitude;

    @Column(name="store_type")
    private String storeType;

    @Column(name="rating")
    private int rating;

    @Column(name="delivery_fee")
    private BigDecimal deliveryFee;
}