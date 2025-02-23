package com.siopa.siopa_stores.controllers;

import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable UUID id) {
        return storeService.getStoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Store> getStoreByEmail(@PathVariable String email) {
        return storeService.getStoreByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        return ResponseEntity.ok(storeService.createStore(store));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable UUID id, @RequestBody Store updatedStore) {
        return ResponseEntity.ok(storeService.updateStore(id, updatedStore));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{storeId}/addOwner/{ownerId}")
    public ResponseEntity<Store> addOwnerToStore(@PathVariable UUID storeId, @PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.addOwnerToStore(storeId, ownerId));
    }

    @PutMapping("/{storeId}/removeOwner/{ownerId}")
    public ResponseEntity<Store> removeOwnerFromStore(@PathVariable UUID storeId, @PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.removeOwnerFromStore(storeId, ownerId));
    }
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Store>> getStoresByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.getStoresByOwner(ownerId));
    }
}