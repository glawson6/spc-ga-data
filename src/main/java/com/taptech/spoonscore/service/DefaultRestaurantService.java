package com.taptech.spoonscore.service;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.domain.RestaurantSearch;
import com.taptech.spoonscore.entity.RestaurantDetails;
import com.taptech.spoonscore.entity.RestaurantLocation;
import com.taptech.spoonscore.entity.ZipCodes;
import com.taptech.spoonscore.locator.LocationService;
import com.taptech.spoonscore.locator.RestaurantLocator;
import com.taptech.spoonscore.repository.ZipCodesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by tap on 10/6/15.
 */
@Service("restaurantService")

@Transactional
public class DefaultRestaurantService  extends AbstractService implements RestaurantService {

    private final Logger logger = LoggerFactory.getLogger(DefaultRestaurantService.class);

    @Inject
    @Qualifier(value = "YelpRestaurantLocator")
    RestaurantLocator yelpRestaurantLocator;

    @Inject
    @Qualifier(value = "GoogleLocationService")
    LocationService locationService;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private ZipCodesRepository zipCodesRepository;

    @Override
    public Collection<Restaurant> findRestaurants(RestaurantSearch restaurantSearch) {
        Collection<Restaurant> restaurants = new ArrayList<Restaurant>();
        Location location = resolveLocation(restaurantSearch);
        logger.info("[-------------Gathering Location Info--------------------");
        logger.info("Searching for Restaurants using {}",restaurantSearch.toString());
        logger.info("Resolved to Location {}",location.toString());
        logger.info("--------------------------------------------------------]");

        Collection<Restaurant> foundRestaurants = yelpRestaurantLocator.locateRestaurants(restaurantSearch);
        //logger.info("{}",foundRestaurants);
        //restaurants.addAll(foundRestaurants);
        for (Restaurant restaurant:foundRestaurants){
            Restaurant dbRestaurant = null;
            try {
                restaurant.getLocation().setCounty(location.getCounty());
                //Thread.sleep(1000);
                // Lets remember if we have a restaurant in the database
                logger.info("[================================================================");
                logger.info("restaurant => {}",restaurant.toString());
                dbRestaurant = mergeRestaurantFromDatabase(restaurant);
                logger.info("dbRestaurant => {}",dbRestaurant.toString());
                logger.info("================================================================]");
                boolean updatedLinks = false;
                if (null == dbRestaurant.getInspectionSearchLink()){
                    Integer zipCode = Integer.parseInt(dbRestaurant.getLocation().getZipCode());
                    String county = dbRestaurant.getLocation().getCounty();
                    String companyName = dbRestaurant.getCompanyName().split(" ")[0];
                    logger.info("Extracting url for zipCode => {}, county => {}, companyName => {}", new Object[]{zipCode, county, companyName});
                    try {
                        String url = createNameSearchURL(zipCode, county, companyName);
                        dbRestaurant.setInspectionSearchLink(url);
                        updatedLinks = true;
                    } catch (Exception e) {
                        logger.error("Exception creating inspection link {}", restaurant.toString(),e);
                    }
                }
                if (null == dbRestaurant.getViewReportLink()){
                    setReportLink(dbRestaurant);
                    updatedLinks = true;
                }

                if (updatedLinks) {
                    saveRestaurantInDatabase(dbRestaurant, dbRestaurant.getStatus());
                }
                restaurants.add(dbRestaurant);

            } catch (Exception e) {
                logger.error("Exception getting inspection data {}", restaurant.toString(),e);
            }

        }

        return restaurants;
    }
    private static final String WILDCARD = "%";
    private Restaurant mergeRestaurantFromDatabase(Restaurant yelpRestaurant) {
        Restaurant restaurant = yelpRestaurant;
        logger.info("------------------------- yelpRestaurant => {}",yelpRestaurant);
        StringBuilder companyNameBuilder = new StringBuilder(yelpRestaurant.getCompanyName().split(" ")[0]);
        String companyName =  companyNameBuilder.insert(0,WILDCARD).append(WILDCARD).toString();
        // companyName = "%White%";
        StringBuilder addressBuilder = new StringBuilder(yelpRestaurant.getCompanyAddress().split(" ")[0]);
        String address = addressBuilder.insert(0,WILDCARD).append(WILDCARD).toString();
        //address = "%3172%";
        List<RestaurantDetails> restaurantDetailsList = findDBRestaurants(companyName, address);
       logger.info("foundDBRestaurants {}",((null == restaurantDetailsList)?"0":restaurantDetailsList.size()));
        if (null != restaurantDetailsList && restaurantDetailsList.size() > 0) {
            // At least one result
            RestaurantDetails restaurantDetails = restaurantDetailsList.get(0);
            RestaurantLocation restaurantLocation = restaurantLocationRepository.findOneByRestaurantId(restaurantDetails.getRestaurantId());
            if (null != restaurantDetails) {
                logger.info("restaurantDetails => {}", restaurantDetails.toString());

                logger.info("[===========================Before Inspection Link {} Report Link {}=====================================",new Object[]{yelpRestaurant.getInspectionLink(),yelpRestaurant.getViewReportLink()});
                restaurant = mergeYelpRestaurantIntoDbRestaurant(yelpRestaurant, restaurantDetails, restaurantLocation);
                logger.info("===========================After Inspection Link {} Report Link {}=====================================]",new Object[]{restaurant.getInspectionLink(),restaurant.getViewReportLink()});

                restaurant.setStatus(1);
                saveRestaurantInDatabase(restaurant,1);

            } else {
                logger.info("No restaurantDetails found in DB!");
                restaurant = yelpRestaurant;
                restaurant.setStatus(2);
                saveRestaurantInDatabase(restaurant,2);
            }
        } else {
            Integer zipCode = Integer.parseInt(yelpRestaurant.getLocation().getZipCode());
            String county = yelpRestaurant.getLocation().getCounty();
            companyName = yelpRestaurant.getCompanyName().split(" ")[0];
            logger.info("Extracting url for zipCode => {}, county => {}, companyName => {}", new Object[]{zipCode, county, companyName});
            String url = null;
            try {
                url = createNameSearchURL(zipCode, county, companyName);
                Map<String, Object> metaData =  extractRestaurantData(url);
                List<Restaurant> restaurants = (List<Restaurant>)metaData.get(InspectionDataService.RESTAURANT_KEY);
                if (null != restaurants && restaurants.size() > 0){
                    for (Restaurant restaurantEXtract:restaurants){
                        saveRestaurantInDatabase(restaurantEXtract,0);
                    }
                    StringBuilder cNameBuilder = new StringBuilder(restaurant.getCompanyName().split(" ")[0]);
                    String cName =  companyNameBuilder.insert(0,WILDCARD).append(WILDCARD).toString();
                    StringBuilder aBuilder = new StringBuilder(restaurant.getCompanyAddress().split(" ")[0]);
                    String addr = addressBuilder.insert(0,WILDCARD).append(WILDCARD).toString();
                    restaurantDetailsList = findDBRestaurants(cName, addr);
                    RestaurantDetails restaurantDetails = restaurantDetailsList.get(0);
                    RestaurantLocation restaurantLocation = restaurantLocationRepository.findOneByRestaurantId(restaurantDetails.getRestaurantId());
                    if (null != restaurantDetails) {
                        logger.info("restaurantDetails => {}", restaurantDetails.toString());
                        logger.info("[===========================Before Inspection Link {} Report Link {}=====================================",new Object[]{yelpRestaurant.getInspectionLink(),yelpRestaurant.getViewReportLink()});
                        restaurant = mergeYelpRestaurantIntoDbRestaurant(yelpRestaurant, restaurantDetails, restaurantLocation);
                        logger.info("===========================After Inspection Link {} Report Link {}=====================================]",new Object[]{restaurant.getInspectionLink(),restaurant.getViewReportLink()});

                        restaurant.setStatus(1);
                        saveRestaurantInDatabase(restaurant,1);


                    } else {
                        logger.info("No restaurantDetails found in DB!");
                        restaurant = yelpRestaurant;
                        restaurant.setStatus(2);
                        saveRestaurantInDatabase(restaurant, 2);
                    }

                } else {
                    logger.info("No restaurants found on search of website found in DB!");
                    restaurant = yelpRestaurant;
                    restaurant.setStatus(2);
                    saveRestaurantInDatabase(restaurant,2);
                }
            } catch (UnsupportedEncodingException e) {
                logger.error("Error extracting url for zipCode => {}, county => {}, companyName => {}", new Object[]{zipCode,county, companyName,e});
            }
        }
        return restaurant;
    }

    private List<RestaurantDetails> findDBRestaurants(String companyName, String address) {
        List<RestaurantDetails> restaurantDetailsList = null;
        TypedQuery<RestaurantDetails> typedQuery = entityManager.createQuery("select rd from RestaurantDetails rd where (rd.companyName like :companyName OR " +
        "rd.altCompanyName like :companyName) AND rd.companyAddress like :companyAddress",RestaurantDetails.class);
        typedQuery.setParameter("companyName",companyName.toString()).setParameter("companyAddress", address.toString());
        logger.info("companyName => {} companyAddress => {} ",companyName.toString(), address.toString());
        restaurantDetailsList = typedQuery.getResultList();
        return restaurantDetailsList;
    }

    private Restaurant mergeYelpRestaurantIntoDbRestaurant(Restaurant yelpRestaurant, RestaurantDetails restaurantDetails, RestaurantLocation restaurantLocation) {
        Restaurant restaurant;
        restaurant = new Restaurant();
        BeanUtils.copyProperties(yelpRestaurant, restaurant);
        BeanUtils.copyProperties(restaurantLocation,restaurant.getLocation());
        restaurant.getLocation().setState(restaurantLocation.getStateAbbrev());
        restaurant.setAlternateCompanyName(restaurantDetails.getCompanyName());
        restaurant.setFoundBy(restaurantDetails.getFoundBy());
        restaurant.setCompanyName(restaurantDetails.getCompanyName());
        restaurant.setCompanyInspectionScore((null != restaurantDetails.getCompanyScore())?restaurantDetails.getCompanyScore().toString():null);
        restaurant.setCompanyInspectionGrade((null != restaurantDetails.getCompanyGrade())?restaurantDetails.getCompanyGrade().toString():null);
        //restaurant.setRatingCommentsLink(restaurantDetails.getRatingCommentsLink());
        restaurant.setInspectionLink(restaurantDetails.getInspectionLink());
        restaurant.setViewReportLink(restaurantDetails.getInspectionReportLink());
        restaurant.setRestaurantID(restaurantDetails.getRestaurantId());
        restaurant.getLocation().setId(restaurantDetails.getRestaurantId());
        return restaurant;
    }

    private Location resolveLocation(RestaurantSearch restaurantSearch) {
        Location location = null;
        if (null != restaurantSearch.getLatitude() && null != restaurantSearch.getLongitude()){
            location = locationService.getLocationByLatLong(restaurantSearch.getLatitude(), restaurantSearch.getLongitude());
        } else if (null != restaurantSearch.getZipCode()){
            location = locationService.getLocationByZipCode(restaurantSearch.getZipCode());
        } else if (null != restaurantSearch.getCity() && null != restaurantSearch.getState()){
            location = locationService.getLocationByCity(restaurantSearch.getCity(), restaurantSearch.getState());
        }
        if (null == location.getCounty()){
            ZipCodes zipCodes = null;
            String county = null;
            if (null != restaurantSearch.getZipCode()) {
                zipCodes = zipCodesRepository.findOneByZipCode(restaurantSearch.getZipCode());
            } else if (null != restaurantSearch.getCity()){
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
    @Override
    public Restaurant updateRestaurant(Restaurant restaurant, boolean update) {
        setReportLink(restaurant);
        // TODO . Maybe some more work needs to be done!
        return restaurant;
    }

    @Override
    public Restaurant updateRestaurant(Restaurant restaurant) {
        setReportLink(restaurant);
        // TODO . Maybe some more work needs to be done!
        return restaurant;
    }
}
