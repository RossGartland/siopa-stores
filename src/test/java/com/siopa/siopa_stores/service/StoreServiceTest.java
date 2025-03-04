package com.siopa.siopa_stores.service;

import com.siopa.siopa_stores.helpers.DistanceHelper;
import com.siopa.siopa_stores.kafka.KafkaProducerService;
import com.siopa.siopa_stores.kafka.OwnerRoleUpdateEvent;
import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.repositories.StoreRepository;
import com.siopa.siopa_stores.requests.LocationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private Store sampleStore;
    private UUID storeId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        storeId = UUID.randomUUID();
        ownerId = UUID.randomUUID();

        sampleStore = new Store();
        sampleStore.setStoreId(storeId);
        sampleStore.setName("Test Store");
        sampleStore.setRegion("Test Region");
        sampleStore.setAddress("Test Address");
        sampleStore.setActive(true);
        sampleStore.setPhoneNumber("1234567890");
        sampleStore.setEmail("test@example.com");
        sampleStore.setOwnerIds(new ArrayList<>());
        sampleStore.setLatitude(55.0);
        sampleStore.setLongitude(-5.0);
    }

    @Test
    void testGetAllStores() {
        when(storeRepository.findAll()).thenReturn(Collections.singletonList(sampleStore));

        List<Store> result = storeService.getAllStores();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(sampleStore, result.get(0));
    }

    @Test
    void testGetStoreById() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(sampleStore));

        Optional<Store> result = storeService.getStoreById(storeId);

        assertTrue(result.isPresent());
        assertEquals(sampleStore, result.get());
    }

    @Test
    void testGetStoreByEmail() {
        when(storeRepository.findByEmail(sampleStore.getEmail())).thenReturn(Optional.of(sampleStore));

        Optional<Store> result = storeService.getStoreByEmail(sampleStore.getEmail());

        assertTrue(result.isPresent());
        assertEquals(sampleStore, result.get());
    }

    @Test
    void testGetActiveStores() {
        when(storeRepository.findByIsActiveTrue()).thenReturn(Collections.singletonList(sampleStore));

        List<Store> result = storeService.getActiveStores();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testCreateStore() {
        when(storeRepository.save(sampleStore)).thenReturn(sampleStore);

        Store result = storeService.createStore(sampleStore);

        assertNotNull(result);
        assertEquals(sampleStore, result);
    }

    @Test
    void testUpdateStore() {
        Store updatedStore = new Store();
        updatedStore.setName("Updated Store");

        when(storeRepository.findById(storeId)).thenReturn(Optional.of(sampleStore));
        when(storeRepository.save(any(Store.class))).thenReturn(updatedStore);

        Store result = storeService.updateStore(storeId, updatedStore);

        assertNotNull(result);
        assertEquals("Updated Store", result.getName());
    }

    @Test
    void testUpdateStoreNotFound() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> storeService.updateStore(storeId, sampleStore));
    }

    @Test
    void testDeleteStore() {
        doNothing().when(storeRepository).deleteById(storeId);

        assertDoesNotThrow(() -> storeService.deleteStore(storeId));
        verify(storeRepository, times(1)).deleteById(storeId);
    }

    @Test
    void testAddOwnerToStore() {
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(sampleStore));
        when(storeRepository.save(sampleStore)).thenReturn(sampleStore);
        doNothing().when(kafkaProducerService).sendRoleUpdateMessage(any(OwnerRoleUpdateEvent.class));

        Store result = storeService.addOwnerToStore(storeId, ownerId);

        assertNotNull(result);
        assertTrue(result.getOwnerIds().contains(ownerId));
    }

    @Test
    void testRemoveOwnerFromStore() {
        sampleStore.getOwnerIds().add(ownerId);
        when(storeRepository.findById(storeId)).thenReturn(Optional.of(sampleStore));
        when(storeRepository.save(sampleStore)).thenReturn(sampleStore);

        Store result = storeService.removeOwnerFromStore(storeId, ownerId);

        assertNotNull(result);
        assertFalse(result.getOwnerIds().contains(ownerId));
    }

    @Test
    void testGetStoresByOwner() {
        when(storeRepository.findByOwnerId(ownerId)).thenReturn(Collections.singletonList(sampleStore));

        List<Store> result = storeService.getStoresByOwner(ownerId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindStoresByLatLng() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setLatitude(55.1);
        locationRequest.setLongitude(-5.1);

        when(storeRepository.findAll()).thenReturn(Collections.singletonList(sampleStore));
        when(distanceHelper.distanceCalculation(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(5.0);

        List<Store> result = storeService.findStoresByLatLng(locationRequest);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindStoresByLatLngNoStoresFound() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setLatitude(60.0);
        locationRequest.setLongitude(-10.0);

        when(storeRepository.findAll()).thenReturn(Collections.singletonList(sampleStore));
        when(distanceHelper.distanceCalculation(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(20.0);

        List<Store> result = storeService.findStoresByLatLng(locationRequest);

        assertTrue(result.isEmpty());
    }
}
