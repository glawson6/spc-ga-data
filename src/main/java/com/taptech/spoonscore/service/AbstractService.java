package com.taptech.spoonscore.service;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.entity.RestaurantDetails;
import com.taptech.spoonscore.entity.RestaurantDetailsStage;
import com.taptech.spoonscore.entity.RestaurantLocation;
import com.taptech.spoonscore.entity.RestaurantLocationStage;
import com.taptech.spoonscore.repository.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tap on 10/6/15.
 */

@Transactional
public class AbstractService {
    public static final String REPORT_LINK_SELECTOR = "div table tbody tr td table tbody tr td.body div.body table tbody tr td.body a";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
    protected static final String NA = "NA";
    // history.cfm?id=886363&amp;inspID=17520594&amp;county=Fulton
    protected static final String A_TAG_END = "</a>";
    protected static final String SCORE_HOLDER = "Score:";
    protected static final String GRADE_HOLDER = "Grade:";
    protected static final String URL_COUNTY = "county=";
    protected static final String HTML_AND = "&";
    protected static final String INSPECTION_ID_STR_HOLDER = "inspID=";
    protected static final String RESTAURANT_ID_STR_HOLDER = "cfm?id=";
    protected static final String EMPTY_STRING = "";
    protected static final String BR_START_TAG = "<br>";
    protected static final String BR_END_TAG = "<br />";
    protected static final String FODD_SERVICE_INSP_STR = "(Food Service Inspections)";
    protected static final String STRONG_TAG_START = "<strong>";
    protected static final String STRONG_TAG_END = "</strong>";
    protected static final Pattern addressPattern = Pattern.compile("(<br>\\s+([\\d]+|[\\w]+|[\\s]+|[,]+|[-]+|[.]+|)+<br>)");
    //  private static final Pattern restaurantNamePattern = Pattern.compile("(<strong>(\\w+|\\s+|\\d+|([']|[(]|[)]|[,]|[-]|[\\/]|[@]|[#]|[.]|[?]|[!]))+<\\/strong><br>)");
    protected static final Pattern restaurantNamePattern = Pattern.compile("(<strong>(([\\w]+|[\\s]+|[\\d]+|[']+|[(]+|[)]+|[,]+|[-]+|[\\/]+|[@]+|[#]+|[.]+|[?]+|[!]+|[+]+|[&]+))+<\\/strong><br>)");
    protected static final Pattern firstInspectionLinkPattern = Pattern.compile("(<br \\/>\\s+<a href=\"([\\d]+|[\\w]+|[.]+|[?]+|[=]+|[&]|[\\s]+|[\"]+|[>]+|[,]+|[:]|)+<\\/a>)");
    protected static final Pattern searchResultsPattern = Pattern.compile("(<h3>Search Results<\\/h3>\\s+<b>\\d+)");
    protected static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    protected static final int FOUND_TEXT_LENGTH = 800;
    protected static final String GEORGIA_SUFFIX = "georgia/";
    protected static final String TEST_URL = "http://ga.healthinspections.us/georgia/search.cfm?start=15141&1=1&f=s&r=zip&s=30315&inspectionType=&sd=09/01/2015&ed=10/01/2015&useDate=NO&county=Fulton&";
    //private static String URL = "http://ga.state.gegov.com/";
    protected static String URL = "http://ga.healthinspections.us/";
    protected static String URL_QUERY = URL + GEORGIA_SUFFIX + "search.cfm?";
    private final Logger logger = LoggerFactory.getLogger(DefaultInspectionDataService.class);

    @Autowired
    protected ZipCodesRepository zipCodesRepository;
    @Autowired
    protected RestaurantDetailsRepository restaurantDetailsRepository;
    @Autowired
    protected RestaurantLocationRepository restaurantLocationRepository;
    @Autowired
    protected RestaurantDetailsStageRepository restaurantDetailsStageRepository;
    @Autowired
    protected RestaurantLocationStageRepository restaurantLocationStageRepository;
    @PersistenceContext
    protected EntityManager entityManager;



    protected String getHTMLPage(String url) throws IOException {
        GenericUrl genericUrl = new GenericUrl(url);
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setRequestMethod("GET");
                    }
                });
        HttpRequest request = requestFactory.buildGetRequest(genericUrl);
        request.setNumberOfRetries(3);
        HttpResponse response = request.execute();
        return response.parseAsString();
    }

    protected String cleanupWhiteSpace(String restaurantName) {
        String clean = restaurantName.trim().replace("\n", "").replace("\r", "");
        return clean;
    }

    public String createNameSearchURL(Integer zipCode, String county, String companyName) throws UnsupportedEncodingException {
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append(URL_QUERY);
        sb.append("1=1&f=s&r=name&s=");
        sb.append(URLEncoder.encode(companyName, "UTF-8"));
        sb.append("&inspectionType=");
        sb.append("&sd=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&ed=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&useDate=NO");
        sb.append("&county=").append(county);
        return sb.toString();
    }

    public String createSearchURL(Integer zipCode, String county, Integer startIndex) {
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append(URL_QUERY);
        sb.append("start=");
        sb.append(startIndex);
        sb.append("&1=1&f=s&r=name&s=");
        sb.append("&inspectionType=Food");
        sb.append("&sd=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&ed=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&useDate=NO");
        sb.append("&county=").append(county);
        return sb.toString();
    }

    public String createSearchURLORIG(Integer zipCode, String county, Integer startIndex) {
        Date now = new Date();
        StringBuilder sb = new StringBuilder();
        sb.append(URL_QUERY);
        sb.append("start=");
        sb.append(startIndex);
        sb.append("&1=1&f=s&r=zip&s=");
        sb.append(zipCode);
        //sb.append("&inspectionType=Food");
        sb.append("&inspectionType=");
        sb.append("&sd=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&ed=").append(SearchUtil.INSPECTION_DATE_FORMATTER.format(now));
        sb.append("&useDate=NO");
        sb.append("&county=").append(county);
        return sb.toString();
    }

    private boolean callFindMatchers(List<Matcher> matchers) {
        boolean match = false;
        for (Matcher matcher : matchers) {
            match = matcher.find();
        }
        return match;
    }

    public void saveRestaurantInStageDatabase(Restaurant restaurant) {
        RestaurantDetailsStage inDBDetails = restaurantDetailsStageRepository.findOneByRestaurantId(restaurant.getRestaurantID());
        if (null == inDBDetails) {
            RestaurantDetailsStage restaurantDetails = extractRestaurantDetailsStage(restaurant);
            RestaurantLocationStage restaurantLocation = extractRestaurantLocationStage(restaurant.getLocation());
            restaurantDetailsStageRepository.save(restaurantDetails);
            restaurantLocationStageRepository.save(restaurantLocation);
        } else {
            inDBDetails.setLastUpdated(new Date());
            inDBDetails.setStatus(1);
            inDBDetails.setCompanyGrade((restaurant.getCompanyInspectionGrade() != null) ? restaurant.getCompanyInspectionGrade().charAt(0) : null);
            inDBDetails.setCompanyScore((null != restaurant.getCompanyInspectionScore()) ? new Integer(restaurant.getCompanyInspectionScore()):null);
            inDBDetails.setFoundByLink(restaurant.getFoundByLink());
            inDBDetails.setInspectionReportLink(restaurant.getViewReportLink());
            entityManager.persist(inDBDetails);
        }
    }

    @Async
    public void saveRestaurantInDatabase(Restaurant restaurant, Integer status) {
        RestaurantDetails inDBDetails = restaurantDetailsRepository.findOneByRestaurantId(restaurant.getRestaurantID());
        if (null == inDBDetails) {
            RestaurantDetails restaurantDetailsExtract = extractRestaurantDetails(restaurant);
            restaurantDetailsExtract.setLastUpdated(new Date());
            restaurantDetailsExtract.setStatus(status);
            RestaurantLocation restaurantLocationExtract = extractRestaurantLocation(restaurant.getLocation());
            restaurantDetailsRepository.save(restaurantDetailsExtract);
            restaurantLocationRepository.save(restaurantLocationExtract);
        } else {
            /*
            inDBDetails.setLastUpdated(new Date());
            inDBDetails.setStatus(1);
            inDBDetails.setCompanyGrade((restaurant.getCompanyInspectionGrade() != null) ? restaurant.getCompanyInspectionGrade().charAt(0) : null);
            inDBDetails.setCompanyScore((null != restaurant.getCompanyInspectionScore()) ? new Integer(restaurant.getCompanyInspectionScore()):null);
            //inDBDetails.setFoundByLink(restaurant.getFoundByLink());
            inDBDetails.setInspectionReportLink(restaurant.getViewReportLink());
            */
           RestaurantLocation restaurantLocation = restaurantLocationRepository.findOneByRestaurantId(restaurant.getRestaurantID());
            extractRestaurantDetails(restaurant, inDBDetails);
            extractRestaurantLocation(restaurant.getLocation(), restaurantLocation);
            entityManager.persist(inDBDetails);
            entityManager.persist(restaurantLocation);
            logger.info("We updated a restaurant with id {}",inDBDetails.getRestaurantId());
        }
    }

    protected RestaurantLocation extractRestaurantLocation(Location location) {
        RestaurantLocation restaurantLocation = new RestaurantLocation();
        this.extractRestaurantLocation(location, restaurantLocation);
        return restaurantLocation;
    }
    protected RestaurantLocation extractRestaurantLocation(Location location, RestaurantLocation restaurantLocation) {
        BeanUtils.copyProperties(location, restaurantLocation);
        restaurantLocation.setRestaurantId(location.getId());
        restaurantLocation.setStateAbbrev(location.getState());
        restaurantLocation.setZipCode(Integer.parseInt(location.getZipCode()));
        logger.info("Persisting restaurantLocation {}", restaurantLocation.toString());
        return restaurantLocation;
    }

    protected RestaurantDetails extractRestaurantDetails(Restaurant restaurant) {
        RestaurantDetails restaurantDetails = new RestaurantDetails();
        this.extractRestaurantDetails(restaurant, restaurantDetails);
        return restaurantDetails;
    }


    protected RestaurantDetails extractRestaurantDetails(Restaurant restaurant,RestaurantDetails restaurantDetails) {
        BeanUtils.copyProperties(restaurant, restaurantDetails);
        restaurantDetails.setRestaurantId(restaurant.getRestaurantID());
        restaurantDetails.setCompanyGrade((restaurant.getCompanyInspectionGrade() != null) ? restaurant.getCompanyInspectionGrade().charAt(0): null);
        restaurantDetails.setCompanyScore((null != restaurant.getCompanyInspectionScore()) ? Integer.parseInt(restaurant.getCompanyInspectionScore()) : 0);
        restaurantDetails.setImageUrl(restaurant.getImageURL());
        restaurantDetails.setInspectionReportLink(restaurant.getViewReportLink());
        restaurantDetails.setStatus(restaurant.getStatus());
        restaurantDetails.setInspectionSearchLink(restaurant.getInspectionSearchLink());
        restaurantDetails.setInspectionLink(restaurant.getInspectionLink());
        logger.info("Persisting restaurantDetails {}", restaurantDetails.toString());
        return restaurantDetails;
    }

    protected RestaurantLocationStage extractRestaurantLocationStage(Location location) {
        RestaurantLocationStage restaurantLocation = new RestaurantLocationStage();
        BeanUtils.copyProperties(location, restaurantLocation);
        restaurantLocation.setRestaurantId(location.getId());
        restaurantLocation.setStateAbbrev(location.getState());
        restaurantLocation.setZipCode(Integer.parseInt(location.getZipCode()));
        logger.info("Persisting restaurantLocation {}", restaurantLocation.toString());
        return restaurantLocation;
    }

    protected RestaurantDetailsStage extractRestaurantDetailsStage(Restaurant restaurant) {
        RestaurantDetailsStage restaurantDetails = new RestaurantDetailsStage();
        BeanUtils.copyProperties(restaurant, restaurantDetails);
        restaurantDetails.setRestaurantId(restaurant.getRestaurantID());
        restaurantDetails.setCompanyGrade(restaurant.getCompanyInspectionGrade().charAt(0));
        restaurantDetails.setCompanyScore(Integer.parseInt(restaurant.getCompanyInspectionScore()));
        restaurantDetails.setImageUrl(restaurant.getImageURL());
        restaurantDetails.setInspectionReportLink(restaurant.getViewReportLink());
        logger.info("Persisting restaurantDetails {}", restaurantDetails.toString());
        return restaurantDetails;
    }

    public void setReportLink(Restaurant restaurant) {
        try{
            String url = restaurant.getInspectionLink();
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();
            Elements reportLinkElements = doc.select(REPORT_LINK_SELECTOR);
            Element reportLinkElement = reportLinkElements.first();
            String hrefValue = reportLinkElement.attr("href");
            String htmlInner = reportLinkElement.html();
            String reportURL = URL + hrefValue;
            logger.info("Report Link {}",reportURL);
            restaurant.setViewReportLink(reportURL);
        } catch (Exception e){

        }
    }

    public Map<String, Object> extractRestaurantData(String url) {
        return this.extractRestaurantData(url,0);
    }

    public Map<String, Object> extractRestaurantData(String url, Integer status) {
        Map<String, Object> metaData = new HashMap<String, Object>();
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        try {
            try {
                logger.info("########################## Pausing HTTP Query!!!!!   #######################");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.error("Error pausing thread!", e);
            }
            metaData.put(InspectionDataService.RESTAURANT_KEY, restaurants);
            metaData.put(InspectionDataService.URL_KEY, url);
            String county = url.substring(url.indexOf(URL_COUNTY) + URL_COUNTY.length());
            String resultsText = getHTMLPage(url);

            metaData.put(InspectionDataService.HTML_PAGE_KEY, resultsText);
            Matcher restaurantNameMatcher = restaurantNamePattern.matcher(resultsText);
            Matcher[] matcherArray = new Matcher[]{restaurantNameMatcher};
            List<Matcher> matchers = Arrays.asList(matcherArray);
            int count = 0;
            while (restaurantNameMatcher.find()) {
                count++;
                String restaurantAddressBlock = "temp";
                String restaurantNameBlock = null;
                String inspectionLinkBlock = null;
                if ((restaurantNameMatcher.start() + FOUND_TEXT_LENGTH) > (resultsText.length() - 1)) {
                    // If this is true our index is greater than the length of the String. We need from the
                    // start of the matcher on...
                    restaurantNameBlock = resultsText.substring(restaurantNameMatcher.start());
                } else {
                    restaurantNameBlock = resultsText.substring(restaurantNameMatcher.start(), restaurantNameMatcher.start() + FOUND_TEXT_LENGTH);
                }
                // This will get the exact String
                int indexForBR = restaurantNameBlock.lastIndexOf(BR_END_TAG);
                // restaurantNameBlock = restaurantNameBlock.substring(0,indexForBR);
                // restaurantNameBlock = restaurantNameBlock.subSequence(0,indexForBR);
                Matcher addressMatcher = addressPattern.matcher(restaurantNameBlock);
                if (addressMatcher.find()) {
                    restaurantAddressBlock = restaurantNameBlock.substring(addressMatcher.start(), addressMatcher.end());
/*
                    logger.info("addressMatcher count {} =>  {}", count, addressMatcher.group());
                    logger.info("Start index: " + addressMatcher.start());
                    logger.info(" End index: " + addressMatcher.end() + " ");
*/
                }

                Matcher inspectionLinkMatcher = firstInspectionLinkPattern.matcher(restaurantNameBlock);
                if (inspectionLinkMatcher.find()) {
                    inspectionLinkBlock = restaurantNameBlock.substring(inspectionLinkMatcher.start(), inspectionLinkMatcher.end());
                }

                logger.debug("Found count {} restaurantNameBlock {} ", new Object[]{count, restaurantNameBlock});
                logger.debug("Found count {} restaurantAddressBlock {} ", new Object[]{count, restaurantAddressBlock});
                logger.info("Found count {} inspectionLinkBlock {} ", new Object[]{count, inspectionLinkBlock});
                String restaurantName = null;
                String restaurantAddress = null;
                String restaurantID = null;
                String lastInspectionID = null;
                String inspectionLink = null;
                String score = null;
                String grade = null;
                if (null != restaurantAddressBlock) {
                    int strongIndex = restaurantNameBlock.indexOf(STRONG_TAG_END);
                    restaurantName = restaurantNameBlock.substring(STRONG_TAG_START.length(), strongIndex);
                    int addressStartIndex = BR_START_TAG.length();
                    int addressEndIndex = restaurantAddressBlock.lastIndexOf(BR_START_TAG);
                    try {
                        restaurantAddress = restaurantAddressBlock.substring(addressStartIndex, addressEndIndex);
                    } catch(Exception e){
                        restaurantAddress = "NA";
                    }
                }
                if (null != restaurantName && restaurantName.contains(FODD_SERVICE_INSP_STR)) {
                    restaurantName = restaurantName.replace(FODD_SERVICE_INSP_STR, EMPTY_STRING);
                    restaurantName = cleanupWhiteSpace(restaurantName);
                }
                if (null != restaurantAddress) {
                    restaurantAddress = cleanupWhiteSpace(restaurantAddress);
                }
                if (null != inspectionLinkBlock) {
                    int restaurantIDIndexStart = inspectionLinkBlock.indexOf(RESTAURANT_ID_STR_HOLDER) + RESTAURANT_ID_STR_HOLDER.length();
                    int restaurantIDIndexEnd = inspectionLinkBlock.indexOf(HTML_AND);
                    restaurantID = inspectionLinkBlock.substring(restaurantIDIndexStart, restaurantIDIndexEnd);
                    String remainingInspectionLink = inspectionLinkBlock.substring(inspectionLinkBlock.indexOf(INSPECTION_ID_STR_HOLDER));
                    int inspectionIDIndexStart = INSPECTION_ID_STR_HOLDER.length();
                    int inspectionIDIndexEnd = remainingInspectionLink.indexOf(HTML_AND);
                    lastInspectionID = remainingInspectionLink.substring(inspectionIDIndexStart, inspectionIDIndexEnd);
                    int inspectionLinkStartIndex = inspectionLinkBlock.indexOf("\"");
                    int inspectionLinkEndIndex = inspectionLinkBlock.lastIndexOf("\"");
                    // http://ga.healthinspections.us/_templates/87/Food/_report_full.cfm?fsimID=19161863&domainID=87&rtype=food
                    // We cannot produce this link from here. We need to go into another page see setInspectionLink .....
                    inspectionLink = URL + GEORGIA_SUFFIX + inspectionLinkBlock.substring(inspectionLinkStartIndex+1, inspectionLinkEndIndex);
                    //inspectionLink = NA;
                    int scoreStartIndex = inspectionLinkBlock.indexOf(SCORE_HOLDER) + SCORE_HOLDER.length();
                    score = inspectionLinkBlock.substring(scoreStartIndex, scoreStartIndex + 4).trim();
                    if (score.contains(",")) {
                        score = score.replace(",", "");
                    }
                    int gradeStartIndex = inspectionLinkBlock.indexOf(GRADE_HOLDER) + GRADE_HOLDER.length();
                    grade = inspectionLinkBlock.substring(gradeStartIndex, inspectionLinkBlock.indexOf(A_TAG_END)).trim();
                }
                logger.debug("Found count {} restaurantName {} ", new Object[]{count, restaurantName});
                logger.debug("Found count {} restaurantAddress {} ", new Object[]{count, restaurantAddress});
                logger.debug("Found count {} restaurantID {} ", new Object[]{count, restaurantID});
                logger.debug("Found count {} lastInspectionID {} ", new Object[]{count, lastInspectionID});
                logger.debug("Found count {} score {} ", new Object[]{count, score});
                logger.debug("Found count {} grade {} ", new Object[]{count, grade});
                Restaurant restaurant = new Restaurant();
                String[] addressTokens = restaurantAddress.split(" ");
                String address = null;
                if (addressTokens.length > 1) {
                    String zipCode = addressTokens[addressTokens.length - 1];
                    String stateCode = addressTokens[addressTokens.length - 2];
                    String city = addressTokens[addressTokens.length - 3];
                    city = city.substring(0,city.length()-1);
                    logger.info("We have city {}",city);
                    address = restaurantAddress.substring(0, restaurantAddress.indexOf(city));
                    restaurant.getLocation().setZipCode(zipCode.substring(0,5));
                    restaurant.getLocation().setState(stateCode);
                    restaurant.getLocation().setAddress(address);
                    restaurant.getLocation().setCity(city);
                    restaurant.setCompanyAddress(address);
                } else {
                    restaurant.getLocation().setZipCode("-1");
                    restaurant.getLocation().setState(NA);
                    restaurant.getLocation().setAddress(NA);
                    restaurant.getLocation().setCity(NA);
                    restaurant.setCompanyAddress(NA);
                }



                restaurant.getLocation().setCounty(county);
                restaurant.getLocation().setId(restaurantID);
                restaurant.getLocation().setLongitude(0.0);
                restaurant.getLocation().setLatitude(0.0);
                restaurant.setLastUpdated(new Date());
                restaurant.setStatus(status);
                restaurant.setCompanyName(restaurantName);
                restaurant.setRestaurantID(restaurantID);
                restaurant.setInspectionLink(inspectionLink);
                restaurant.setCompanyInspectionGrade(grade);
                restaurant.setCompanyInspectionScore(score);
                restaurant.setFoundByLink(url);
                Integer zipCode = null;
                String companyName = null;
                try{

                    zipCode = Integer.parseInt(restaurant.getLocation().getZipCode());
                    companyName = restaurant.getCompanyName().split(" ")[0];
                    String searchURL = this.createNameSearchURL(zipCode, county, companyName);
                    restaurant.setInspectionSearchLink(searchURL);

                } catch(Exception e){
                    logger.warn("Could not extract url for zipCode => {}, county => {}, companyName => {}", new Object[]{zipCode, county, companyName});
                }

                restaurants.add(restaurant);
            }
            metaData.put(InspectionDataService.NOFR_KEY, count);
            if (count < 20) {
                logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~URLS lower than 20 {}", url);
            }
        } catch (IOException e) {
            logger.error("Exception ", e);
            metaData.put(InspectionDataService.EXCEPTION_KEY, e);
        }
        return metaData;
    }
}
