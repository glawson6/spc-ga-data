package com.taptech.spoonscore.service;

import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;

import java.util.Collection;

/**
 * Created by tap on 3/29/15.
 */
public interface RestaurantService {

    Collection<Restaurant> findRestaurants(RestaurantSearch restaurantSearch);
    Restaurant updateRestaurant(Restaurant restaurant, boolean update);

    Restaurant updateRestaurant(Restaurant restaurant);
}
