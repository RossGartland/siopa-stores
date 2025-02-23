package com.siopa.siopa_stores.service;

import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.repositories.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

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
            store.getOwnerIds().add(ownerId);
            return storeRepository.save(store);
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
}
