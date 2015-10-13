package com.taptech.spoonscore.repository;

import com.taptech.spoonscore.entity.RestaurantDetailsStage;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by tap on 4/3/15.
 */
public interface RestaurantDetailsStageRepository extends JpaRepository<RestaurantDetailsStage, Long> {

    public RestaurantDetailsStage findOneByRestaurantId(String restaurantID);
}
