package com.siopa.siopa_stores.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a request for latitude and longitude.
 */
@Getter
@Setter
public class LocationRequest {
    public double latitude;
    public double longitude;
}