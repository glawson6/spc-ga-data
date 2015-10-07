package com.taptech.spoonscore.service;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.entity.*;
import com.taptech.spoonscore.repository.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tap on 10/2/15.
 */
@Service("inspectionDataService")
@Transactional
public class DefaultInspectionDataService extends AbstractService implements InspectionDataService {

    private final Logger logger = LoggerFactory.getLogger(DefaultInspectionDataService.class);
    // http://ga.healthinspections.us/georgia/search.cfm?1=1&f=s&r=name&s=&inspectionType=Food&sd=09/04/2015&ed=10/04/2015&useDate=NO&county=Worth
    // http://ga.healthinspections.us/georgia/search.cfm?start=1&1=1&f=s&r=name&s=&inspectionType=Food&sd=09/04/2015&ed=10/04/2015&useDate=NO&county=Worth
    // http://ga.healthinspections.us/georgia/search.cfm?start=21&1=1&f=s&r=name&s=&inspectionType=Food&sd=09/04/2015&ed=10/04/2015&useDate=NO&county=Worth&

    // Name Search URL
    //http://ga.healthinspections.us/georgia/search.cfm?1=1&f=s&r=name&s=A%20Bag%20To%20Go&inspectionType=&sd=09/04/2015&ed=10/04/2015&useDate=NO&county=WORTH


    @Override
    public void saveRestaurantInDatabase(Restaurant restaurant, Integer status) {
       super.saveRestaurantInDatabase(restaurant, status);
    }

    private static final String VALID_ZIP_CODES_QUERY = "select zc from ZipCodes zc where zc.county NOT in ('BANKS','BARROW','CLARKE','COBB','DAWSON','DEKALB','DOUGLAS','ELBERT','FORSYTH','FRANKLIN','GREENE','GWINNETT','HABERSHAM','HALL','HART','JACKSON','LUMPKIN','MADISON','MORGAN','OCONEE','OGLETHORPE','RABUN','STEPHENS','TOWNS','UNION','WALTON','WHITE')";

    @Override
    public List<Integer> getValidZipCodes() {
        List<Integer> zipCodes = new ArrayList<Integer>();
        TypedQuery<ZipCodes> codesTypedQuery = entityManager.createQuery(VALID_ZIP_CODES_QUERY, ZipCodes.class);
        List<ZipCodes> zipCodesList = codesTypedQuery.getResultList();
        for (ZipCodes zCodes:zipCodesList){
            zipCodes.add(zCodes.getZipCode());
        }
        return zipCodes;
    }

    public Map<String, Object> getZipCodeMetaData(Integer zipCode) {
        Map<String, Object> metaData = null;
        try {
            try {
                logger.info("########################## Pausing HTTP Query!!!!!   #######################");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Error pausing thread!", e);
            }
            ZipCodes zipCodes = zipCodesRepository.findOneByZipCode(zipCode);
            String county = zipCodes.getCounty();
            String url = this.createSearchURLORIG(zipCode, county, 1);
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Query URL {} ", url);
            String resultsText = getHTMLPage(url);

            metaData = new HashMap<String, Object>();
            Matcher resultsMatcher = searchResultsPattern.matcher(resultsText);
            Integer nofResults = null;
            String resultsStr = null;
            if (resultsMatcher.find()) {
                resultsStr = resultsText.substring(resultsMatcher.start(), resultsMatcher.end());
                String nofResultsStr = resultsStr.substring(resultsStr.lastIndexOf("<b>") + 3);
                logger.debug("Number of Results Str: {}", nofResultsStr);
                nofResults = new Integer(nofResultsStr);
                logger.info("Number of Results: {}", nofResults);
                List<String> urlList = new ArrayList<String>();
                for (int i = 1; i <= nofResults; i = i + 20) {
                    logger.info("Start Page index {}", i);
                    String urlStr = this.createSearchURLORIG(zipCode, county, i);
                    urlList.add(urlStr);
                }
                metaData.put("URLS", urlList);
            }

        } catch (Exception e) {
            logger.error("Exception ", e);
            throw new DataExtractionExcepton(e);
        }
        return metaData;
    }

    public Map<String, Object> getZipCodeMetaData(Integer zipCode, Boolean useDate) {
        Map<String, Object> metaData = null;
        try {
            try {
                logger.info("########################## Pausing HTTP Query!!!!!   #######################");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Error pausing thread!", e);
            }
            ZipCodes zipCodes = zipCodesRepository.findOneByZipCode(zipCode);
            String county = zipCodes.getCounty();
            String url = this.createSearchURLORIG(zipCode, county, 1);
            logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Query URL {} ", url);

            String resultsText = getHTMLPage(url);

            metaData = zipCodeMetaData(zipCode, county, resultsText);

        } catch (Exception e) {
            logger.error("Exception ", e);
            throw new DataExtractionExcepton(e);
        }
        return metaData;
    }

    protected Map<String, Object> zipCodeMetaData(Integer zipCode, String county, String resultsText) {
        Map<String, Object> metaData;
        metaData = new HashMap<String, Object>();
        Matcher resultsMatcher = searchResultsPattern.matcher(resultsText);
        Integer nofResults = null;
        String resultsStr = null;
        if (resultsMatcher.find()) {
            resultsStr = resultsText.substring(resultsMatcher.start(), resultsMatcher.end());
            String nofResultsStr = resultsStr.substring(resultsStr.lastIndexOf("<b>") + 3);
            logger.debug("Number of Results Str: {}", nofResultsStr);
            nofResults = new Integer(nofResultsStr);
            logger.info("Number of Results: {}", nofResults);
            List<String> urlList = new ArrayList<String>();
            for (int i = 1; i <= nofResults; i = i + 20) {
                logger.info("Start Page index {}", i);
                String urlStr = this.createSearchURL(zipCode, county, i);
                urlList.add(urlStr);
            }
            metaData.put("URLS", urlList);
        }
        return metaData;
    }

}
