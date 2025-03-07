package com.siopa.siopa_stores.service;

import com.siopa.siopa_stores.helpers.DistanceHelper;
import com.siopa.siopa_stores.kafka.KafkaProducerService;
import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.repositories.StoreRepository;
import com.siopa.siopa_stores.requests.LocationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link StoreService}.
 */
@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private DistanceHelper distanceHelper;

    @InjectMocks
    private StoreService storeService;

    private Store store;
    private UUID storeId;
    private UUID ownerId;

    /**
     * Sets up test data before each test case.
     */
    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        store = Store.builder()
                .storeId(storeId)
                .name("Test Store")
                .region("Test Region")
                .address("123 Test Street")
                .isActive(true)
                .phoneNumber("1234567890")
                .email("test@store.com")
                .ownerIds(new ArrayList<>(List.of(ownerId)))
                .latitude(40.7128)
                .longitude(-74.0060)
                .storeType("Grocery")
                .rating(5)
                .deliveryFee(BigDecimal.valueOf(5.99))
                .build();
    }

    /**
     * Tests retrieval of all stores.
     */
    @Test
    void getAllStores_ShouldReturnListOfStores() {
        when(storeRepository.findAll()).thenReturn(List.of(store));

        List<Store> result = storeService.getAllStores();

        assertEquals(1, result.size());
        assertEquals("Test Store", result.get(0).getName());
        verify(storeRepository, times(1)).findAll();
    }

    /**
     * Tests retrieval of a store by ID.
     */
    @Test
    void getStoreById_ShouldReturnStore_WhenFound() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));

        Optional<Store> result = storeService.getStoreById(storeId);

        assertTrue(result.isPresent());
        assertEquals("Test Store", result.get().getName());
        verify(storeRepository, times(1)).findById(storeId);
    }

    /**
     * Tests retrieval of a store by email.
     */
    @Test
    void getStoreByEmail_ShouldReturnStore_WhenFound() {
        when(storeRepository.findByEmail(store.getEmail())).thenReturn(Optional.of(store));

        Optional<Store> result = storeService.getStoreByEmail(store.getEmail());

        assertTrue(result.isPresent());
        assertEquals("Test Store", result.get().getName());
        verify(storeRepository, times(1)).findByEmail(store.getEmail());
    }

    /**
     * Tests retrieval of active stores.
     */
    @Test
    void getActiveStores_ShouldReturnListOfActiveStores() {
        when(storeRepository.findByIsActiveTrue()).thenReturn(List.of(store));

        List<Store> result = storeService.getActiveStores();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
        verify(storeRepository, times(1)).findByIsActiveTrue();
    }

    /**
     * Tests creation of a new store.
     */
    @Test
    void createStore_ShouldSaveAndReturnStore() {
        when(storeRepository.save(store)).thenReturn(store);

        Store result = storeService.createStore(store);

        assertNotNull(result);
        assertEquals("Test Store", result.getName());
        verify(storeRepository, times(1)).save(store);
    }

    /**
     * Tests updating an existing store.
     */
    @Test
    void updateStore_ShouldUpdateAndReturnStore_WhenStoreExists() {
        Store updatedStore = store;
        updatedStore.setName("Updated Store");

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(updatedStore);

        Store result = storeService.updateStore(storeId, updatedStore);

        assertEquals("Updated Store", result.getName());
        verify(storeRepository, times(1)).save(store);
    }

    /**
     * Tests deleting a store by ID.
     */
    @Test
    void deleteStore_ShouldDeleteStore_WhenStoreExists() {
        doNothing().when(storeRepository).deleteById(storeId);

        storeService.deleteStore(storeId);

        verify(storeRepository, times(1)).deleteById(storeId);
    }

    /**
     * Tests adding an owner to a store.
     */
    @Test
    void addOwnerToStore_ShouldAddOwnerAndReturnStore() {
        UUID newOwnerId = UUID.randomUUID();
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        Store result = storeService.addOwnerToStore(storeId, newOwnerId);

        assertTrue(result.getOwnerIds().contains(newOwnerId));
        verify(storeRepository, times(1)).save(store);
    }

    /**
     * Tests removing an owner from a store.
     */
    @Test
    void removeOwnerFromStore_ShouldRemoveOwnerAndReturnStore() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(store));
        when(storeRepository.save(any(Store.class))).thenReturn(store);

        Store result = storeService.removeOwnerFromStore(storeId, ownerId);

        assertFalse(result.getOwnerIds().contains(ownerId));
        verify(storeRepository, times(1)).save(store);
    }

    /**
     * Tests retrieving stores by owner ID.
     */
    @Test
    void getStoresByOwner_ShouldReturnListOfStores() {
        when(storeRepository.findByOwnerId(ownerId)).thenReturn(List.of(store));

        List<Store> result = storeService.getStoresByOwner(ownerId);

        assertEquals(1, result.size());
        assertEquals(storeId, result.get(0).getStoreId());
        verify(storeRepository, times(1)).findByOwnerId(ownerId);
    }

    /**
     * Tests finding stores within a given location radius.
     */
    @Test
    void findStoresByLatLng_ShouldReturnNearbyStores() {
        LocationRequest locationRequest = new LocationRequest(40.7128, -74.0060);
        when(storeRepository.findAll()).thenReturn(List.of(store));
        when(distanceHelper.distanceCalculation(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(5.0);

        List<Store> result = storeService.findStoresByLatLng(locationRequest);

        assertEquals(1, result.size());
        verify(storeRepository, times(1)).findAll();
        verify(distanceHelper, times(1)).distanceCalculation(anyDouble(), anyDouble(), anyDouble(), anyDouble());
    }
}
