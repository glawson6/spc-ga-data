package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Location;

/**
 * Created by tap on 4/1/15.
 */
public interface LocationService {
    public Location getLocationByZipCode(Integer zipCode);
    public Location getLocationByAddress(String address);
    public Location getLocationByCity(String city, String state);
    public Location getLocationByLatLong(Double latitude, Double longitude);
    public String getName();
}
