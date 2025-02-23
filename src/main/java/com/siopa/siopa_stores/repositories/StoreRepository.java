package com.siopa.siopa_stores.repositories;

import com.siopa.siopa_stores.models.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID> {
    Optional<Store> findByEmail(String email);
    List<Store> findByIsActiveTrue();

    @Query("SELECT s FROM Store s JOIN s.ownerIds o WHERE o = :ownerId")
    List<Store> findByOwnerId(UUID ownerId);
}
