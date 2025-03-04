package com.siopa.siopa_stores.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.siopa.siopa_stores.models.Store;
import com.siopa.siopa_stores.requests.LocationRequest;
import com.siopa.siopa_stores.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StoreController.class)
class StoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreService storeService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void testGetAllStores() throws Exception {
        when(storeService.getAllStores()).thenReturn(Collections.singletonList(sampleStore));

        mockMvc.perform(get("/api/stores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(sampleStore.getName()));
    }

    @Test
    void testGetStoreById_Found() throws Exception {
        when(storeService.getStoreById(storeId)).thenReturn(Optional.of(sampleStore));

        mockMvc.perform(get("/api/stores/{id}", storeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleStore.getName()));
    }

    @Test
    void testGetStoreById_NotFound() throws Exception {
        when(storeService.getStoreById(storeId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stores/{id}", storeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetStoreByEmail_Found() throws Exception {
        when(storeService.getStoreByEmail(sampleStore.getEmail())).thenReturn(Optional.of(sampleStore));

        mockMvc.perform(get("/api/stores/email/{email}", sampleStore.getEmail()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(sampleStore.getEmail()));
    }

    @Test
    void testGetStoreByEmail_NotFound() throws Exception {
        when(storeService.getStoreByEmail(sampleStore.getEmail())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/stores/email/{email}", sampleStore.getEmail()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateStore() throws Exception {
        when(storeService.createStore(any(Store.class))).thenReturn(sampleStore);

        mockMvc.perform(post("/api/stores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStore)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleStore.getName()));
    }

    @Test
    void testUpdateStore() throws Exception {
        when(storeService.updateStore(any(UUID.class), any(Store.class))).thenReturn(sampleStore);

        mockMvc.perform(put("/api/stores/{id}", storeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleStore)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleStore.getName()));
    }

    @Test
    void testDeleteStore() throws Exception {
        Mockito.doNothing().when(storeService).deleteStore(storeId);

        mockMvc.perform(delete("/api/stores/{id}", storeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddOwnerToStore() throws Exception {
        when(storeService.addOwnerToStore(storeId, ownerId)).thenReturn(sampleStore);

        mockMvc.perform(put("/api/stores/{storeId}/addOwner/{ownerId}", storeId, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleStore.getName()));
    }

    @Test
    void testRemoveOwnerFromStore() throws Exception {
        when(storeService.removeOwnerFromStore(storeId, ownerId)).thenReturn(sampleStore);

        mockMvc.perform(put("/api/stores/{storeId}/removeOwner/{ownerId}", storeId, ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(sampleStore.getName()));
    }

    @Test
    void testGetStoresByOwner() throws Exception {
        when(storeService.getStoresByOwner(ownerId)).thenReturn(Collections.singletonList(sampleStore));

        mockMvc.perform(get("/api/stores/owner/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value(sampleStore.getName()));
    }

    @Test
    void testGetNearbyStores_Found() throws Exception {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setLatitude(55.1);
        locationRequest.setLongitude(-5.1);

        when(storeService.findStoresByLatLng(any(LocationRequest.class)))
                .thenReturn(Collections.singletonList(sampleStore));

        mockMvc.perform(post("/api/stores/nearby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void testGetNearbyStores_NotFound() throws Exception {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setLatitude(60.0);
        locationRequest.setLongitude(-10.0);

        when(storeService.findStoresByLatLng(any(LocationRequest.class))).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/api/stores/nearby")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(locationRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Sorry, there are no stores in your area."));
    }
}
