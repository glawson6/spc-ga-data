package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;

import java.util.Collection;
import java.util.Map;

/**
 * Created by tap on 3/29/15.
 */
public interface RestaurantLocator {
    public Collection<Restaurant> locateRestaurants(RestaurantSearch restaurantSearch);
    public Collection<Restaurant> locateRestaurants(Location location, Integer offset, Integer pageSize);
    public String getName();
    public String setLocatorConfiguration(Map<String, Object> config);
}
