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

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final KafkaProducerService kafkaProducerService;
    private final DistanceHelper distanceHelper;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    public Optional<Store> getStoreById(UUID storeId) {
        return storeRepository.findById(storeId);
    }

    public Optional<Store> getStoreByEmail(String email) {
        return storeRepository.findByEmail(email);
    }

    public List<Store> getActiveStores() {
        return storeRepository.findByIsActiveTrue();
    }

    @Transactional
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

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

    @Transactional
    public void deleteStore(UUID storeId) {
        storeRepository.deleteById(storeId);
    }

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

    @Transactional
    public Store removeOwnerFromStore(UUID storeId, UUID ownerId) {
        return storeRepository.findById(storeId).map(store -> {
            store.getOwnerIds().remove(ownerId);
            return storeRepository.save(store);
        }).orElseThrow(() -> new RuntimeException("Store not found"));
    }

    public List<Store> getStoresByOwner(UUID ownerId) {
        return storeRepository.findByOwnerId(ownerId);
    }

    /**
     * Finds all stores within a 10-mile radius of the given longitude and latitude.
     * @param locationRequest
     * @return
     */
    public List<Store> findStoresByLatLng(LocationRequest locationRequest) {
        List<Store> storeList = storeRepository.findAll(); //Get all stores
        List<Store> nearbyStores = new ArrayList(); //Initialize an empty list to store valid stores.
        double distanceCalculated; //The distance between 2 points.
        for (Store store: storeList) {
            distanceCalculated = distanceHelper.distanceCalculation(store.getLatitude(), locationRequest.latitude, store.getLongitude(), locationRequest.longitude); //Calculate the distance.
            if(distanceCalculated < 10) {
                nearbyStores.add(store); //Add store to list if it is within a distance.
            }
        }
        return nearbyStores;
    }
}
