package com.taptech.spoonscore.service;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by tap on 3/29/15.
 */
@Service("yelpRestaurantUpdate")
public class YelpRestaurantCombineUpdate implements RestaurantCombineUpdate {

    private final Logger logger = LoggerFactory.getLogger(YelpRestaurantCombineUpdate.class);

    private static final String NAME = "YELP";
    private static final String RESPONSE_NAME_KEY = "name";

    @Inject
    private Environment env;

    private static String API_HOST = "api.yelp.com";
    private static String DEFAULT_TERM = "food";
    private static Integer SEARCH_LIMIT = 20;
    private static final String SEARCH_PATH = "/v2/search";
    private static final String BUSINESS_PATH = "/v2/business";
    private static final String SPACE = " ";

    @Inject
    OAuthService service;
    @Inject
    Token accessToken;


    @Override
    public void combineRestaurantData(Restaurant restaurant) {

        String searchResponseJSON = null;
        JSONObject response = null;
        try {
            JSONParser parser = new JSONParser();
            StringBuilder stringBuilder = new StringBuilder(restaurant.getCompanyAddress().trim());
            stringBuilder.append(SPACE).append(restaurant.getLocation().getCity()).append(",");
            stringBuilder.append(SPACE).append(restaurant.getLocation().getState()).append(SPACE);
            stringBuilder.append(restaurant.getLocation().getZipCode());
            logger.info("Location created for Yelp Query {}",stringBuilder.toString());
            searchResponseJSON = searchForBusinessesByLocation(restaurant.getCompanyName(),stringBuilder.toString());
            response = (JSONObject) parser.parse(searchResponseJSON);
            //System.out.println("searchResponseJSON => "+searchResponseJSON);
        } catch (ParseException pe) {
            logger.error("Error: could not parse JSON response:");
            logger.error(searchResponseJSON);
        }

        JSONArray businesses = (JSONArray) response.get("businesses");
        if (null != businesses && businesses.size() > 0) {
            JSONObject business = (JSONObject) businesses.get(0);
            restaurant.setFoundBy(NAME);
            logger.debug("Yelp business found => {}", business.toJSONString());
            JSONObject location = (JSONObject)business.get("location");
            JSONObject coordinateObject = (JSONObject) location.get("coordinate");
            logger.debug("coordinateObject => {}",location.get("coordinate").toString());
            Double latitude = Double.parseDouble(coordinateObject.get("latitude").toString());
            Double longitude = Double.parseDouble(coordinateObject.get("longitude").toString());
            restaurant.setAlternateCompanyName(business.get(RESPONSE_NAME_KEY).toString());
            restaurant.getLocation().setLatitude(latitude);
            restaurant.getLocation().setLongitude(longitude);
            restaurant.setCompanyPhone(getJSONValue("display_phone",business));
            restaurant.setRatingCommentsLink(getJSONValue("url",business));
            Float rating = Float.parseFloat(getJSONValue("rating", business));
            restaurant.setRating(rating);
            restaurant.setImageURL(getJSONValue("image_url", business));
            restaurant.setLastUpdated(new Date());
        }

    }

    /*
    @Override
    public void combineRestaurantData(Location location) {

        String searchResponseJSON = null;
        JSONObject response = null;
        try {
            JSONParser parser = new JSONParser();
            StringBuilder stringBuilder = new StringBuilder(location.getAddress().trim());
            stringBuilder.append(SPACE).append(location.getCity()).append(",");
            stringBuilder.append(SPACE).append(location.getState()).append(SPACE);
            stringBuilder.append(location.getZipCode());
            logger.info("Location created for Yelp Query {}",stringBuilder.toString());
            searchResponseJSON = searchForBusinessesByLocation(restaurant.getCompanyName(),stringBuilder.toString());
            response = (JSONObject) parser.parse(searchResponseJSON);
            //System.out.println("searchResponseJSON => "+searchResponseJSON);
        } catch (ParseException pe) {
            logger.error("Error: could not parse JSON response:");
            logger.error(searchResponseJSON);
        }

        JSONArray businesses = (JSONArray) response.get("businesses");
        if (null != businesses && businesses.size() > 0) {
            JSONObject business = (JSONObject) businesses.get(0);
            restaurant.setFoundBy(NAME);
            logger.debug("Yelp business found => {}", business.toJSONString());
            JSONObject location = (JSONObject)business.get("location");
            JSONObject coordinateObject = (JSONObject) location.get("coordinate");
            logger.debug("coordinateObject => {}",location.get("coordinate").toString());
            Double latitude = Double.parseDouble(coordinateObject.get("latitude").toString());
            Double longitude = Double.parseDouble(coordinateObject.get("longitude").toString());
            restaurant.setAlternateCompanyName(business.get(RESPONSE_NAME_KEY).toString());
            restaurant.getLocation().setLatitude(latitude);
            restaurant.getLocation().setLongitude(longitude);
            restaurant.setCompanyPhone(getJSONValue("display_phone",business));
            restaurant.setRatingCommentsLink(getJSONValue("url",business));
            Float rating = Float.parseFloat(getJSONValue("rating", business));
            restaurant.setRating(rating);
            restaurant.setImageURL(getJSONValue("image_url", business));
            restaurant.setLastUpdated(new Date());
        }

    }
    */

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
        logger.info("Querying {}",request.getCompleteUrl());
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
        logger.info("Querying {}",request.getCompleteUrl());
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

}
