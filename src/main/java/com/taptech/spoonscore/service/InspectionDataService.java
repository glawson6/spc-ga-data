package com.taptech.spoonscore.service;

import com.taptech.spoonscore.domain.Restaurant;

import java.util.List;
import java.util.Map;

/**
 * Created by tap on 10/2/15.
 */
public interface InspectionDataService {
    Map<String,Object> getZipCodeMetaData(Integer zipCode);
    Map<String, Object> getZipCodeMetaData(Integer zipCode, Boolean useDate);
    Map<String, Object> extractRestaurantData(String url);
    Map<String, Object> extractRestaurantData(String url, Integer status);
    public void saveRestaurantInStageDatabase(Restaurant restaurant);
    public void saveRestaurantInDatabase(Restaurant restaurant, Integer status);
    void setReportLink(Restaurant restaurant);

    public List<Integer> getValidZipCodes();
    public final static String RESTAURANT_KEY = "restaurant.key";
    public final static String NOFR_KEY = "nofr.key";
    public final static String EXCEPTION_KEY = "exception.key";
    public final static String HTML_PAGE_KEY = "html.page.key";
    public final static String URL_KEY = "url.key";
}
