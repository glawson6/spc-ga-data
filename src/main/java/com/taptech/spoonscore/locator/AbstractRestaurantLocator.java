package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;
import com.taptech.spoonscore.entity.ZipCodes;
import com.taptech.spoonscore.repository.ZipCodesRepository;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

/**
 * Created by tap on 4/2/15.
 */
public abstract class AbstractRestaurantLocator implements RestaurantLocator {

    @Inject
    @Qualifier(value = "GoogleLocationService")
    LocationService locationService;

    @Inject
    private ZipCodesRepository zipCodesRepository;


    public Collection<Restaurant> locateRestaurants(Location location, Integer offset, Integer pageSize) {
        RestaurantSearch search = new RestaurantSearch();
        updateRestaurantSearchWithLocation(location, offset, pageSize, search);
        return locateRestaurants(search);
    }

    public void updateRestaurantSearchWithLocation(Location location, Integer offset, Integer pageSize, RestaurantSearch search) {
        search.setLatitude(location.getLatitude());
        search.setLongitude(location.getLongitude());
        search.setCity(location.getCity());
        if (null != location.getZipCode()) {
            search.setZipCode(Integer.parseInt(location.getZipCode()));
        }
        search.setCounty(location.getCounty());
        search.setState(location.getState());
        search.setOffset(offset);
        search.setPageSize(pageSize);
    }

    /**
     * We only have minimal location data from serach. We need to get as much info about locations as
     * needed.
     * @param restaurantSearch
     * @return
     */
    public Location resolveLocation(RestaurantSearch restaurantSearch) {
        Location location = null;
        if (null != restaurantSearch.getLatitude() && null != restaurantSearch.getLongitude()){
            location = locationService.getLocationByLatLong(restaurantSearch.getLatitude(), restaurantSearch.getLongitude());
        } else if (null != restaurantSearch.getZipCode()){
            location = locationService.getLocationByZipCode(restaurantSearch.getZipCode());
        } else if (null != restaurantSearch.getCity() && null != restaurantSearch.getState()){
            location = locationService.getLocationByCity(restaurantSearch.getCity(), restaurantSearch.getState());
        }
        // locationService did not have ZipCode information, we will find it in our database.
        if (null == location.getCounty()){
            ZipCodes zipCodes = null;
            String county = null;
            if (null != restaurantSearch.getZipCode()) {
                zipCodes = zipCodesRepository.findOneByZipCode(restaurantSearch.getZipCode());
            } else if (null != restaurantSearch.getCity()){
                // This may not be correct, but we are trying to coerce a county from a City.
                List<ZipCodes> zipCodesList = zipCodesRepository.findAllByCity(restaurantSearch.getCity().toUpperCase());
                zipCodes = zipCodesList.get(0);
                county = zipCodesList.get(0).getCounty();
            }
            //log.debug("{} ", zipCodes.toString());
            county = zipCodes.getCounty();
            location.setCounty(county);
            //restaurantSearch.setCounty(county);
        }
        return location;
    }
}
