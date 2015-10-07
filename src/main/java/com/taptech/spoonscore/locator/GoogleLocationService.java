package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.Request;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by tap on 4/1/15.
 */
@Service("GoogleLocationService")
public class GoogleLocationService implements LocationService{
    private final Logger log = LoggerFactory.getLogger(GoogleLocationService.class);

    @Inject
    Environment env;

    private static final String GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String NAME = "GoogleLocationService";
    private static final String FOUND_BY = "GOOGLE";

    @Override
    public Location getLocationByZipCode(Integer zipCode) {
        Request request = new Request(Verb.GET, GEOCODE_URL);
        request.addQuerystringParameter("address", zipCode.toString());
        Location location = getLocationFromRequest(request,true);
        log.info("Using ####################### zipCode {}", zipCode);
        log.info("Got   ####################### Location {}",location);
        return location;
    }

    @Override
    public Location getLocationByAddress(String address) {
        Request request = new Request(Verb.GET, GEOCODE_URL);
        request.addQuerystringParameter("address", address);
        Location location = getLocationFromRequest(request,true);
        log.info("Using ####################### address {}", address);
        log.info("Got   ####################### Location {}",location);
        return location;
    }

    @Override
    public Location getLocationByCity(String city, String state) {
        Request request = new Request(Verb.GET, GEOCODE_URL);
        request.addQuerystringParameter("address",city+","+state);
        Location location = getLocationFromRequest(request,true);
        log.info("Using ####################### city {} and state {}", new Object[]{city, state});
        log.info("Got   ####################### Location {}",location);
        return location;
    }

    @Override
    public Location getLocationByLatLong(Double latitude, Double longitude) {
        Request request = new Request(Verb.GET, GEOCODE_URL);
        //request.addQuerystringParameter("limit", SEARCH_LIMIT.toString());

        String latlng = latitude.toString()+","+longitude.toString();
        request.addQuerystringParameter("latlng", latlng);
        Location location = getLocationFromRequest(request,false);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        log.info("Using ####################### latitude {} and longitude {}", new Object[]{latitude, longitude});
        log.info("Got   ####################### Location {}",location);
        return location;
    }

    private Location getLocationFromRequest(Request request, boolean setLatLong) {
        request.addQuerystringParameter("key", env.getProperty("GOOGLE_API_KEY"));
        log.debug("URL sent to Google {}",request.getCompleteUrl());
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
        JSONArray jsonResults = (JSONArray)response.get("results");
        // Value at zero index is closest to specified latitude and longitude
        return extractLocation((JSONObject)jsonResults.get(0),setLatLong);
    }

    @Override
    public String getName() {
        return NAME;
    }

    private Location extractLocation(JSONObject jsonLocation,boolean setLatLong) {
        Location location = new Location();
        location.setFoundBy(FOUND_BY);
        location.setAddress(jsonLocation.get("formatted_address").toString());
        location.setId(jsonLocation.get("place_id").toString());
        String countyStr = findAddrComponents((JSONArray) jsonLocation.get("address_components"), "administrative_area_level_2");
        String stateStr = findAddrComponentsShortName((JSONArray) jsonLocation.get("address_components"), "administrative_area_level_1");
        String cityStr = findAddrComponents((JSONArray) jsonLocation.get("address_components"), "locality");
        String zipCodeStr = findAddrComponents((JSONArray) jsonLocation.get("address_components"), "postal_code");
        String county = countyStr != null ? countyStr.split(" ")[0].toUpperCase() : null;

        location.setState(stateStr);
        location.setCounty(county);
        location.setCity(cityStr.toUpperCase());
        location.setZipCode(zipCodeStr);
        if (setLatLong) {
            JSONObject geometry = (JSONObject) jsonLocation.get("geometry");
            JSONObject locationInnerJSON = (JSONObject) geometry.get("location");
            String latStr = locationInnerJSON.get("lat").toString();
            String longStr = locationInnerJSON.get("lng").toString();
            Double latitude = Double.parseDouble(latStr);
            Double longitude = Double.parseDouble(longStr);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        return location;
    }

    private String findAddrComponents(JSONArray addressComponents, String type) {
        String component = null;
        for (int i = 0; i < addressComponents.size(); i++){
            JSONObject addressComponent = (JSONObject)addressComponents.get(i);
            JSONArray typeArray = (JSONArray)addressComponent.get("types");
            if (typeArray.contains(type)){
                component = addressComponent.get("long_name").toString();
            }
        }
        return component;
    }

    private String findAddrComponentsShortName(JSONArray addressComponents, String type) {
        String component = null;
        for (int i = 0; i < addressComponents.size(); i++){
            JSONObject addressComponent = (JSONObject)addressComponents.get(i);
            JSONArray typeArray = (JSONArray)addressComponent.get("types");
            if (typeArray.contains(type)){
                component = addressComponent.get("short_name").toString();
            }
        }
        return component;
    }

}
