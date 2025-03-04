package com.siopa.siopa_stores.helpers;

import org.springframework.stereotype.Service;

/**
 * Helper class for calculating distance between 2 points.
 */
@Service
public class DistanceHelper {
    /**
     * The Haversine method is used to calculate the distance between 2 points by providing their respective latitude and longitude.
     * The output is measured in miles.
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @return
     */
    public double distanceCalculation(double lat1, double lat2, double lon1,
                                      double lon2) {
        final int R = 6371; // Earths radius.

        double latDistance = Math.toRadians(lat2 - lat1); //Convert angle measured in degrees to an approximately equivalent angle measured in radians.
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = (R * c * 1000) * 0.000621371192; //Calculates distance in miles.

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }
}