package com.siopa.siopa_stores.repositories;

import com.siopa.siopa_stores.models.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on the {@link Store} entity.
 */
public interface StoreRepository extends JpaRepository<Store, UUID> {

    /**
     * Finds a store by its email.
     *
     * @param email the email of the store.
     * @return an {@link Optional} containing the store if found, otherwise empty.
     */
    Optional<Store> findByEmail(String email);

    /**
     * Retrieves all active stores.
     *
     * @return a list of stores that are currently active.
     */
    List<Store> findByIsActiveTrue();

    /**
     * Finds all stores owned by a specific owner.
     *
     * @param ownerId the unique identifier of the owner.
     * @return a list of stores owned by the specified owner.
     */
    @Query("SELECT s FROM Store s JOIN s.ownerIds o WHERE o = :ownerId")
    List<Store> findByOwnerId(UUID ownerId);
}
