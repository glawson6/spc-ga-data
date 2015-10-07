package com.taptech.spoonscore.service;

/**
 * Created by tap on 10/3/15.
 */

import com.taptech.spoonscore.ApplicationTest;
import com.taptech.spoonscore.domain.Restaurant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

/**
 * Created by tap on 3/26/15.
 * mvn -Dtest=com.taptech.spoonscore.service.DefaultInspectionDataServiceTest test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ApplicationTest.class)
@IntegrationTest
public class DefaultInspectionDataServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DefaultInspectionDataServiceTest.class);

    private static final String TEST_URL = "http://ga.healthinspections.us/georgia/search.cfm?start=15141&1=1&f=s&r=zip&s=30315&inspectionType=&sd=09/01/2015&ed=10/01/2015&useDate=NO&county=Fulton&";
    static{
        System.setProperty("DATABASE_URL","postgres://ttis:ttis@centos-vm-local:5432/spoonscore");
        System.setProperty("spring.profiles.active","dev");
        System.setProperty("http.port","8080");
    }

    @Autowired
    InspectionDataService inspectionDataService;

    @Test
    public void testSanity(){
        logger.info("We are sane!!!!!!");
    }

    @Test
    public void testGetCountyFromZipCode(){
      Integer zipCode = 30315;
        Map<String, Object> metaData  = inspectionDataService.getZipCodeMetaData(30315);
        List<String> urls = (List<String>)metaData.get("URLS");
        for (String url:urls){
            logger.info("URL created {}",url);
        }
    }

    @Test
    public void testExtractRestaurantData(){
        Map<String, Object> metaData = inspectionDataService.extractRestaurantData(TEST_URL);
        logger.info("Meta Data returned {}",metaData.size());
    }

}
