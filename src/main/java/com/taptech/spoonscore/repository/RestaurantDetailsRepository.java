package com.taptech.spoonscore.repository;

import com.taptech.spoonscore.entity.RestaurantDetails;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tap on 4/3/15.
 */
public interface RestaurantDetailsRepository extends JpaRepository<RestaurantDetails, Long> {

    public RestaurantDetails findOneByRestaurantId(String restaurantID);
}
