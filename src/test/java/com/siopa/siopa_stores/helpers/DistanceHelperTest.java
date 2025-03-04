package com.siopa.siopa_stores.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceHelperTest {

    private DistanceHelper distanceHelper;

    @BeforeEach
    void setUp() {
        distanceHelper = new DistanceHelper();
    }

    @Test
    void testDistanceCalculation_SameLocation() {
        double distance = distanceHelper.distanceCalculation(40.7128, 40.7128, -74.0060, -74.0060);
        assertEquals(0.0, distance, 0.0001, "Distance should be 0 for the same coordinates.");
    }

    @Test
    void testDistanceCalculation_ShortDistance() {
        double distance = distanceHelper.distanceCalculation(40.7128, 40.7138, -74.0060, -74.0070);
        assertTrue(distance > 0, "Distance should be greater than 0 for nearby locations.");
    }

    @Test
    void testDistanceCalculation_LongDistance() {
        double distance = distanceHelper.distanceCalculation(40.7128, 34.0522, -74.0060, -118.2437);
        assertEquals(2445.0, distance, 50.0, "Distance between New York and Los Angeles should be around 2445 miles.");
    }

    @Test
    void testDistanceCalculation_Hemispheres() {
        double distance = distanceHelper.distanceCalculation(51.5074, -33.8688, -0.1278, 151.2093);
        assertEquals(10500.0, distance, 100.0, "Distance between London and Sydney should be around 10,500 miles.");
    }

    @Test
    void testDistanceCalculation_ZeroLatDifference() {
        double distance = distanceHelper.distanceCalculation(40.7128, 40.7128, -74.0060, -73.0060);
        assertTrue(distance > 50, "Distance should be greater than 50 miles for 1-degree longitude difference.");
    }

    @Test
    void testDistanceCalculation_ZeroLonDifference() {
        double distance = distanceHelper.distanceCalculation(40.7128, 41.7128, -74.0060, -74.0060);
        assertTrue(distance > 50, "Distance should be greater than 50 miles for 1-degree latitude difference.");
    }
}
