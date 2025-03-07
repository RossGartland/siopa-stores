package com.siopa.siopa_stores.controllers;

import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.requests.LocationRequest;
import com.siopa.siopa_stores.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller class for managing store-related operations.
 */
@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /**
     * Retrieves all stores.
     *
     * @return a ResponseEntity containing a list of all stores.
     */
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        return ResponseEntity.ok(storeService.getAllStores());
    }

    /**
     * Retrieves a store by its unique identifier.
     *
     * @param id the unique identifier of the store.
     * @return a ResponseEntity containing the store if found, otherwise a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Store> getStoreById(@PathVariable UUID id) {
        return storeService.getStoreById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a store by its email.
     *
     * @param email the email of the store.
     * @return a ResponseEntity containing the store if found, otherwise a 404 Not Found response.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Store> getStoreByEmail(@PathVariable String email) {
        return storeService.getStoreByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new store.
     *
     * @param store the store object to be created.
     * @return a ResponseEntity containing the created store.
     */
    @PostMapping
    public ResponseEntity<Store> createStore(@RequestBody Store store) {
        return ResponseEntity.ok(storeService.createStore(store));
    }

    /**
     * Updates an existing store with the given identifier.
     *
     * @param id the unique identifier of the store to be updated.
     * @param updatedStore the store object containing updated information.
     * @return a ResponseEntity containing the updated store.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Store> updateStore(@PathVariable UUID id, @RequestBody Store updatedStore) {
        return ResponseEntity.ok(storeService.updateStore(id, updatedStore));
    }

    /**
     * Deletes a store by its unique identifier.
     *
     * @param id the unique identifier of the store to be deleted.
     * @return a ResponseEntity with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID id) {
        storeService.deleteStore(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds an owner to a store.
     *
     * @param storeId the unique identifier of the store.
     * @param ownerId the unique identifier of the owner to be added.
     * @return a ResponseEntity containing the updated store.
     */
    @PutMapping("/{storeId}/addOwner/{ownerId}")
    public ResponseEntity<Store> addOwnerToStore(@PathVariable UUID storeId, @PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.addOwnerToStore(storeId, ownerId));
    }

    /**
     * Removes an owner from a store.
     *
     * @param storeId the unique identifier of the store.
     * @param ownerId the unique identifier of the owner to be removed.
     * @return a ResponseEntity containing the updated store.
     */
    @PutMapping("/{storeId}/removeOwner/{ownerId}")
    public ResponseEntity<Store> removeOwnerFromStore(@PathVariable UUID storeId, @PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.removeOwnerFromStore(storeId, ownerId));
    }

    /**
     * Retrieves stores associated with a specific owner.
     *
     * @param ownerId the unique identifier of the owner.
     * @return a ResponseEntity containing a list of stores owned by the specified owner.
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Store>> getStoresByOwner(@PathVariable UUID ownerId) {
        return ResponseEntity.ok(storeService.getStoresByOwner(ownerId));
    }

    /**
     * Finds stores near a given location.
     *
     * @param locationRequest the location request object containing latitude and longitude.
     * @return a ResponseEntity containing a list of nearby stores if found, otherwise a 404 Not Found response with a message.
     */
    @PostMapping("/nearby")
    public ResponseEntity<?> getNearbyStores(@RequestBody LocationRequest locationRequest) {
        List<Store> stores = storeService.findStoresByLatLng(locationRequest);

        if (stores.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sorry, there are no stores in your area.");
        }

        return ResponseEntity.ok(stores);
    }
}
