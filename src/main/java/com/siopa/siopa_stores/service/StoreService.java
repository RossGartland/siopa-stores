package com.siopa.siopa_stores.service;

import com.siopa.siopa_stores.helpers.DistanceHelper;
import com.siopa.siopa_stores.kafka.OwnerRoleUpdateEvent;
import com.siopa.siopa_stores.kafka.KafkaProducerService;
import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.repositories.StoreRepository;
import com.siopa.siopa_stores.requests.LocationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service class responsible for handling store-related operations.
 */
@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DistanceHelper distanceHelper;

    /**
     * Retrieves all stores.
     *
     * @return a list of all stores.
     */
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    /**
     * Retrieves a store by its unique identifier.
     *
     * @param storeId the UUID of the store.
     * @return an {@link Optional} containing the store if found, otherwise empty.
     */
    public Optional<Store> getStoreById(UUID storeId) {
        return storeRepository.findById(storeId);
    }

    /**
     * Retrieves a store by its email.
     *
     * @param email the email address of the store.
     * @return an {@link Optional} containing the store if found, otherwise empty.
     */
    public Optional<Store> getStoreByEmail(String email) {
        return storeRepository.findByEmail(email);
    }

    /**
     * Retrieves all active stores.
     *
     * @return a list of active stores.
     */
    public List<Store> getActiveStores() {
        return storeRepository.findByIsActiveTrue();
    }

    /**
     * Creates a new store and saves it to the database.
     *
     * @param store the store object to be created.
     * @return the created store entity.
     */
    @Transactional
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    /**
     * Updates an existing store with new details.
     *
     * @param storeId       the unique identifier of the store to be updated.
     * @param updatedStore  the store object containing updated details.
     * @return the updated store entity.
     * @throws RuntimeException if the store is not found.
     */
    @Transactional
    public Store updateStore(UUID storeId, Store updatedStore) {
        return storeRepository.findById(storeId).map(store -> {
            store.setName(updatedStore.getName());
            store.setRegion(updatedStore.getRegion());
            store.setAddress(updatedStore.getAddress());
            store.setActive(updatedStore.isActive());
            store.setPhoneNumber(updatedStore.getPhoneNumber());
            store.setEmail(updatedStore.getEmail());
            store.setOwnerIds(updatedStore.getOwnerIds());
            store.setDeliveryFee(updatedStore.getDeliveryFee());
            store.setStoreType(updatedStore.getStoreType());
            return storeRepository.save(store);
        }).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    /**
     * Deletes a store by its unique identifier.
     *
     * @param storeId the unique identifier of the store to be deleted.
     */
    @Transactional
    public void deleteStore(UUID storeId) {
        storeRepository.deleteById(storeId);
    }

    /**
     * Adds an owner to a store.
     *
     * @param storeId the unique identifier of the store.
     * @param ownerId the unique identifier of the owner to be added.
     * @return the updated store entity.
     * @throws RuntimeException if the store is not found.
     */
    @Transactional
    public Store addOwnerToStore(UUID storeId, UUID ownerId) {
        return storeRepository.findById(storeId).map(store -> {
            if (!store.getOwnerIds().contains(ownerId)) {
                store.getOwnerIds().add(ownerId);
                storeRepository.save(store);

                // 🔹 Produce Kafka Event
                OwnerRoleUpdateEvent event = new OwnerRoleUpdateEvent(ownerId, "OWNER");
                kafkaProducerService.sendRoleUpdateMessage(event);
            }
            return store;
        }).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    /**
     * Removes an owner from a store.
     *
     * @param storeId the unique identifier of the store.
     * @param ownerId the unique identifier of the owner to be removed.
     * @return the updated store entity.
     * @throws RuntimeException if the store is not found.
     */
    @Transactional
    public Store removeOwnerFromStore(UUID storeId, UUID ownerId) {
        return storeRepository.findById(storeId).map(store -> {
            store.getOwnerIds().remove(ownerId);
            return storeRepository.save(store);
        }).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    /**
     * Retrieves a list of stores owned by a specific owner.
     *
     * @param ownerId the unique identifier of the owner.
     * @return a list of stores owned by the specified owner.
     */
    public List<Store> getStoresByOwner(UUID ownerId) {
        return storeRepository.findByOwnerId(ownerId);
    }

    /**
     * Finds all stores within a 10-mile radius of the given latitude and longitude.
     *
     * @param locationRequest the request containing latitude and longitude.
     * @return a list of nearby stores within the specified radius.
     */
    public List<Store> findStoresByLatLng(LocationRequest locationRequest) {
        List<Store> storeList = storeRepository.findAll(); // Get all stores
        List<Store> nearbyStores = new ArrayList<>(); // Initialize an empty list to store valid stores.
        double distanceCalculated; // The distance between two points.

        for (Store store : storeList) {
            distanceCalculated = distanceHelper.distanceCalculation(
                    store.getLatitude(), locationRequest.latitude,
                    store.getLongitude(), locationRequest.longitude
            ); // Calculate the distance.

            if (distanceCalculated < 10) {
                nearbyStores.add(store); // Add store to list if it is within the distance.
            }
        }
        return nearbyStores;
    }
}
