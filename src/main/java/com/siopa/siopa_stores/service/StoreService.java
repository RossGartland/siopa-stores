package com.siopa.siopa_stores.service;

import com.siopa.siopa_stores.helpers.DistanceHelper;
import com.siopa.siopa_stores.kafka.OwnerRoleUpdateEvent;
import com.siopa.siopa_stores.kafka.KafkaProducerService;
import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.repositories.StoreRepository;
import com.siopa.siopa_stores.requests.LocationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    private final StoreRepository storeRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DistanceHelper distanceHelper;

    /**
     * Retrieves all stores.
     *
     * @return a list of all stores.
     */
    public List<Store> getAllStores() {
        logger.info("Fetching all stores");
        List<Store> stores = storeRepository.findAll();
        logger.debug("Retrieved {} stores from the database", stores.size());
        return stores;
    }

    /**
     * Retrieves a store by its unique identifier.
     *
     * @param storeId the UUID of the store.
     * @return an {@link Optional} containing the store if found, otherwise empty.
     */
    public Optional<Store> getStoreById(UUID storeId) {
        logger.info("Fetching store with ID: {}", storeId);
        Optional<Store> store = storeRepository.findById(storeId);
        if (store.isPresent()) {
            logger.debug("Store found: {}", store.get());
        } else {
            logger.warn("Store with ID {} not found", storeId);
        }
        return store;
    }

    /**
     * Retrieves a store by its email.
     *
     * @param email the email address of the store.
     * @return an {@link Optional} containing the store if found, otherwise empty.
     */
    public Optional<Store> getStoreByEmail(String email) {
        logger.info("Fetching store with email: {}", email);
        return storeRepository.findByEmail(email);
    }

    /**
     * Retrieves all active stores.
     *
     * @return a list of active stores.
     */
    public List<Store> getActiveStores() {
        logger.info("Fetching all active stores");
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
        logger.info("Creating a new store: {}", store.getName());
        Store savedStore = storeRepository.save(store);
        logger.info("Store created successfully with ID: {}", savedStore.getStoreId());
        return savedStore;
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
        logger.info("Updating store with ID: {}", storeId);

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

            Store updated = storeRepository.save(store);
            logger.info("Store ID {} updated successfully", storeId);
            return updated;
        }).orElseThrow(() -> {
            logger.error("Store with ID {} not found for update", storeId);
            return new RuntimeException("Store not found");
        });
    }

    /**
     * Deletes a store by its unique identifier.
     *
     * @param storeId the unique identifier of the store to be deleted.
     */
    @Transactional
    public void deleteStore(UUID storeId) {
        logger.warn("Deleting store with ID: {}", storeId);
        storeRepository.deleteById(storeId);
        logger.info("Store with ID {} deleted successfully", storeId);
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
        logger.info("Adding owner with ID {} to store ID {}", ownerId, storeId);

        return storeRepository.findById(storeId).map(store -> {
            if (!store.getOwnerIds().contains(ownerId)) {
                store.getOwnerIds().add(ownerId);
                storeRepository.save(store);

                logger.info("Owner ID {} added to Store ID {}", ownerId, storeId);

                // 🔹 Produce Kafka Event
                OwnerRoleUpdateEvent event = new OwnerRoleUpdateEvent(ownerId, "OWNER");
                kafkaProducerService.sendRoleUpdateMessage(event);
                logger.info("Kafka event sent for owner ID {} role update", ownerId);
            }
            return store;
        }).orElseThrow(() -> {
            logger.error("Store with ID {} not found when adding owner {}", storeId, ownerId);
            return new RuntimeException("Store not found");
        });
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
        logger.info("Removing owner with ID {} from store ID {}", ownerId, storeId);

        return storeRepository.findById(storeId).map(store -> {
            store.getOwnerIds().remove(ownerId);
            Store updated = storeRepository.save(store);
            logger.info("Owner ID {} removed from Store ID {}", ownerId, storeId);
            return updated;
        }).orElseThrow(() -> {
            logger.error("Store with ID {} not found when removing owner {}", storeId, ownerId);
            return new RuntimeException("Store not found");
        });
    }

    /**
     * Retrieves a list of stores owned by a specific owner.
     *
     * @param ownerId the unique identifier of the owner.
     * @return a list of stores owned by the specified owner.
     */
    public List<Store> getStoresByOwner(UUID ownerId) {
        logger.info("Fetching stores for owner ID: {}", ownerId);
        return storeRepository.findByOwnerId(ownerId);
    }

    /**
     * Finds all stores within a 10-mile radius of the given latitude and longitude.
     *
     * @param locationRequest the request containing latitude and longitude.
     * @return a list of nearby stores within the specified radius.
     */
    public List<Store> findStoresByLatLng(LocationRequest locationRequest) {
        logger.info("Finding stores near latitude: {}, longitude: {}", locationRequest.latitude, locationRequest.longitude);

        List<Store> storeList = storeRepository.findAll();
        List<Store> nearbyStores = new ArrayList<>();

        for (Store store : storeList) {
            double distance = distanceHelper.distanceCalculation(
                    store.getLatitude(), locationRequest.latitude,
                    store.getLongitude(), locationRequest.longitude);

            if (distance < 10) {
                nearbyStores.add(store);
            }
        }
        logger.info("Found {} stores within 10 miles", nearbyStores.size());
        return nearbyStores;
    }
}
