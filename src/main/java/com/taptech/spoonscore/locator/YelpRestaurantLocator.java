package com.taptech.spoonscore.locator;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by tap on 3/29/15.
 */
@Service("YelpRestaurantLocator")
public class YelpRestaurantLocator extends AbstractRestaurantLocator {

    private final Logger log = LoggerFactory.getLogger(YelpRestaurantLocator.class);

    private static final String NAME = "YELP";
    @Autowired
    private Environment env;

    //private static final String API_HOST = "api.yelp.com";
    //private static final String DEFAULT_TERM = "dinner";
    //private static final String DEFAULT_LOCATION = "San Francisco, CA";
    //private static final int SEARCH_LIMIT = 20;
    //private static final String SEARCH_PATH = "/v2/search";
    //private static final String BUSINESS_PATH = "/v2/business";

    private static String API_HOST = "api.yelp.com";
    private static String DEFAULT_TERM = "food";
    //private static String DEFAULT_LOCATION = "San Francisco, CA";
    private static Integer SEARCH_LIMIT = 20;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";
    Map<String,Object> config = new HashMap<String, Object>();

    @Autowired
    OAuthService service;
    @Autowired
    Token accessToken;


    @Override
    public Collection<Restaurant> locateRestaurants(RestaurantSearch restaurantSearch) {

        Location location = resolveLocation(restaurantSearch);
        Collection<Restaurant> restaurants = new ArrayList<Restaurant>();
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", DEFAULT_TERM);
        if (null != location.getLongitude() && null != location.getLatitude()){
            StringBuilder sb = new StringBuilder(location.getLatitude().toString());
            sb.append(",").append(location.getLongitude().toString());
            request.addQuerystringParameter("ll", sb.toString());
        }
        else if (null != location.getZipCode()){
            request.addQuerystringParameter("location", location.getZipCode().toString());
        } else if (null != location.getCity()) {
            request.addQuerystringParameter("location", location.getCity());
        }
        request.addQuerystringParameter("limit", SEARCH_LIMIT.toString());
        if (null != restaurantSearch.getOffset()) {
            request.addQuerystringParameter("offset", restaurantSearch.getOffset().toString());
        }
        log.info("Querying {}",request.getCompleteUrl());
        this.service.signRequest(this.accessToken, request);
        Response firstResponse = request.send();
        String searchResponseJSON = firstResponse.getBody();

        JSONParser parser = new JSONParser();
        JSONObject response = null;
        try {
            response = (JSONObject) parser.parse(searchResponseJSON);
            //System.out.println("searchResponseJSON => "+searchResponseJSON);
        } catch (ParseException pe) {
            log.error("Error: could not parse JSON response:");
            log.error(searchResponseJSON);
        }

        JSONArray businesses = (JSONArray) response.get("businesses");

        for (int i = 0; i < businesses.size(); i++){
            JSONObject business = (JSONObject) businesses.get(i);
            Restaurant restaurant = createRestaurant(business,location);
            restaurant.setFoundBy(NAME);
            if (null != restaurant.getLocation().getCounty()) {
                restaurant.getLocation().setCounty(location.getCounty());
            }
            restaurants.add(restaurant);
        }

        return restaurants;
    }

    private static final String RESPONSE_NAME_KEY = "name";
    private Restaurant createRestaurant(JSONObject business, Location dmnLocation) {
        log.debug("Yelp business found => {}", business.toJSONString());
        JSONObject location = (JSONObject)business.get("location");
        JSONObject coordinateObject = (JSONObject) location.get("coordinate");
        log.debug("coordinateObject => {}",location.get("coordinate").toString());
        Double latitude = Double.parseDouble(coordinateObject.get("latitude").toString());
        Double longitude = Double.parseDouble(coordinateObject.get("longitude").toString());
        JSONArray address = (JSONArray)location.get("display_address");
        StringBuilder addressBuilder = new StringBuilder();
        for (int i = 0; i < address.size(); i++){
            //JSONObject addressObject = (JSONObject) address.get(i);
            addressBuilder.append(address.get(i)).append(" ");
        }
        Restaurant restaurant = new Restaurant();
        restaurant.setCompanyName(business.get(RESPONSE_NAME_KEY).toString());
        restaurant.getLocation().setLatitude(latitude);
        restaurant.getLocation().setLongitude(longitude);
        restaurant.getLocation().setZipCode(location.get("postal_code").toString());
        restaurant.getLocation().setCity(location.get("city").toString());
        restaurant.setCompanyAddress(addressBuilder.toString());
        String id = getJSONValue("id", business);
        restaurant.setRestaurantID(id);
        restaurant.getLocation().setId(id);
        restaurant.setCompanyPhone(getJSONValue("display_phone",business));
        restaurant.setRatingCommentsLink(getJSONValue("url",business));
        Float rating = Float.parseFloat(getJSONValue("rating", business));
        restaurant.setRating(rating);
        restaurant.setImageURL(getJSONValue("image_url", business));
        restaurant.getLocation().setState(dmnLocation.getState());
        restaurant.setStatus(1);
        restaurant.setLastUpdated(new Date());
        restaurant.setFoundBy("YELP");
        return restaurant;
    }

    private String getJSONValue(String key, JSONObject jsonObject){
        String value = null;
        if (null != jsonObject){
            Object hold = jsonObject.get(key);
            if (null != hold){
                value = hold.toString();
            }
        }
        return value;
    }

    /**
     * Creates and sends a request to the Search API by term and location.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
     * for more info.
     *
     * @param term <tt>String</tt> of the search term to be queried
     * @param location <tt>String</tt> of the location
     * @return <tt>String</tt> JSON Response
     */
    public String searchForBusinessesByLocation(String term, String location) {
        OAuthRequest request = createOAuthRequest(SEARCH_PATH);
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("limit", SEARCH_LIMIT.toString());
        log.info("Querying {}",request.getCompleteUrl());
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }


    /**
     * Creates and sends a request to the Business API by business ID.
     * <p>
     * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
     * for more info.
     *
     * @param businessID <tt>String</tt> business ID of the requested business
     * @return <tt>String</tt> JSON Response
     */
    public String searchByBusinessId(String businessID) {
        OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
        return sendRequestAndGetResponse(request);
    }

    /**
     * Creates and returns an {@link org.scribe.model.OAuthRequest} based on the API endpoint specified.
     *
     * @param path API endpoint to be queried
     * @return <tt>OAuthRequest</tt>
     */
    private OAuthRequest createOAuthRequest(String path) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
        return request;
    }

    /**
     * Sends an {@link org.scribe.model.OAuthRequest} and returns the {@link org.scribe.model.Response} body.
     *
     * @param request {@link org.scribe.model.OAuthRequest} corresponding to the API request
     * @return <tt>String</tt> body of API response
     */
    private String sendRequestAndGetResponse(OAuthRequest request) {
        log.info("Querying {}",request.getCompleteUrl());
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String setLocatorConfiguration(Map<String, Object> config) {
        this.config = config;
        return "OK";
    }
}
