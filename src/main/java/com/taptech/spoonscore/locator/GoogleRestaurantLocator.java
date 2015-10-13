package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;
import com.taptech.spoonscore.entity.ZipCodes;
import com.taptech.spoonscore.repository.ZipCodesRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.Request;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by tap on 3/29/15.
 */
@Service("GoogleRestaurantLocator")
public class GoogleRestaurantLocator extends AbstractRestaurantLocator {
    private final Logger log = LoggerFactory.getLogger(GoogleRestaurantLocator.class);

    @Autowired
    Environment env;

    @Autowired
    private ZipCodesRepository zipCodesRepository;

    public static final String TYPES = "food|cafe|restaurant";
    public static final String NEARBY_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String PLACES_URL = "https://maps.googleapis.com/maps/api/place/details/json";

    private static Map<String, Object> GOOGLE_CONFIG = new HashMap<String, Object>();
    @Override
    public Collection<Restaurant> locateRestaurants(RestaurantSearch restaurantSearch) {
        Request request = new Request(Verb.GET, NEARBY_SEARCH_URL);
        //request.addQuerystringParameter("limit", SEARCH_LIMIT.toString());
        String location = null;
        log.info("Using restaurantSearch {}",restaurantSearch.toString());
        if (null != restaurantSearch.getLatitude() && null != restaurantSearch.getLongitude()){
            location = restaurantSearch.getLatitude()+","+restaurantSearch.getLongitude();
        } else if (restaurantSearch.getZipCode() != null){
            ZipCodes zipCodes = zipCodesRepository.findOneByZipCode(restaurantSearch.getZipCode());
            location = String.valueOf(zipCodes.getLatitude()) + ","+String.valueOf(zipCodes.getLongitude());
        } else if (restaurantSearch.getCity() != null){
            List<ZipCodes> zipCodesList = zipCodesRepository.findAllByCity(restaurantSearch.getCity().toUpperCase());
            ZipCodes zipCodes = zipCodesList.get(0);
            location = String.valueOf(zipCodes.getLatitude()) + ","+String.valueOf(zipCodes.getLongitude());
        }
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("key", env.getProperty("GOOGLE_API_KEY"));
        request.addQuerystringParameter("radius", "200");
        request.addQuerystringParameter("types", TYPES);
        log.debug("URL sent {}",request.getCompleteUrl());
        Response firstResponse = request.send();
        String searchResponseJSON = firstResponse.getBody();
        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(searchResponseJSON);
            //log.info("searchResponseJSON => "+searchResponseJSON);
        } catch (ParseException pe) {
            log.error("Error: could not parse JSON response:");
            log.error(searchResponseJSON,pe);
        }
        Collection<Restaurant> restaurants = new ArrayList<Restaurant>();
        JSONArray jsonResults = (JSONArray)response.get("results");
        List<String> placesIDs = extractPlacesIDs(jsonResults);
        for (String placeID : placesIDs){
        try {
            Request placesRequest = new Request(Verb.GET, PLACES_URL);
            placesRequest.addQuerystringParameter("placeid", placeID);
            placesRequest.addQuerystringParameter("key", env.getProperty("GOOGLE_API_KEY"));
            Response placesResponse = placesRequest.send();
            String placesResponseJSON = placesResponse.getBody();
            JSONObject placesJSONObject = (JSONObject) parser.parse(placesResponseJSON);
            Restaurant restaurant = extractRestaurant(placesJSONObject,restaurantSearch);
            restaurants.add(restaurant);
            //log.info("placesResponseJSON => "+placesResponseJSON);
        } catch (ParseException pe) {
            log.error("Error: could not parse JSON response:");
            log.error(searchResponseJSON,pe);
        }
        }
        return restaurants;
    }

    private static final String NAME = "GOOGLE";
    private Restaurant extractRestaurant(JSONObject placesJSONObject, RestaurantSearch restaurantSearch) {
        Restaurant restaurant = new Restaurant();
        JSONObject restaurantJSON = (JSONObject)placesJSONObject.get("result");
        restaurant.setRestaurantID(restaurantJSON.get("place_id").toString());
        //restaurant.setCompanyAddress(restaurantJSON.get("adr_address").toString());
        restaurant.setCompanyAddress(restaurantJSON.get("formatted_address").toString());
        restaurant.setCompanyName(restaurantJSON.get("name").toString());
        restaurant.setFoundBy(NAME);
        String vicinity = restaurantJSON.get("vicinity").toString();
        String city = vicinity.contains(",") ? vicinity.split(",")[1] : vicinity;
        restaurant.getLocation().setCity(city.trim().toUpperCase());
        JSONObject geometry = (JSONObject)restaurantJSON.get("geometry");
        JSONObject location = (JSONObject)geometry.get("location");
        String latStr = location.get("lat").toString();
        String longStr = location.get("lng").toString();
        Double latitude = Double.parseDouble(latStr);
        Double longitude = Double.parseDouble(longStr);
        restaurant.getLocation().setLongitude(longitude);
        restaurant.getLocation().setLatitude(latitude);
       // restaurant.setRating(Float.parseFloat(restaurantJSON.get("user_ratings_total").toString()));
        return restaurant;
    }

    private List<String> extractPlacesIDs(JSONArray jsonResults) {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < jsonResults.size(); i++){
            JSONObject placeObject = (JSONObject)jsonResults.get(i);
            ids.add(placeObject.get("place_id").toString());
        }
        return ids;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    @Inject
    @Qualifier(value = "GoogleConfig")
    public String setLocatorConfiguration(Map<String, Object> config) {
        GOOGLE_CONFIG = config;
        log.info("######################## WE CAN DO THIS!!!! ##################");
        return "OK";
    }
}
