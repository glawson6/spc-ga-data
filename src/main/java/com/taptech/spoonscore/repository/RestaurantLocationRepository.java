package com.taptech.spoonscore.repository;

import com.taptech.spoonscore.entity.RestaurantLocation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tap on 4/3/15.
 */
public interface RestaurantLocationRepository extends JpaRepository<RestaurantLocation, Long> {
    public RestaurantLocation findOneByRestaurantId(String restaurantID);
}
