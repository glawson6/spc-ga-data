package com.taptech.spoonscore.repository;

import com.taptech.spoonscore.entity.RestaurantDetailsStage;
import com.taptech.spoonscore.entity.RestaurantLocation;
import com.taptech.spoonscore.entity.RestaurantLocationStage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tap on 4/3/15.
 */
public interface RestaurantLocationStageRepository extends JpaRepository<RestaurantLocationStage, Long> {
    public RestaurantLocationStage findOneByRestaurantId(String restaurantID);
}
