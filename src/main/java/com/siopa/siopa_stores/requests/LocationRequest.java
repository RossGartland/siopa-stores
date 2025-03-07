package com.siopa.siopa_stores.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a request for latitude and longitude.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {
    public double latitude;
    public double longitude;
}