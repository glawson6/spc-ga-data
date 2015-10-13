package com.taptech.spoonscore.mule.transformers;

import com.taptech.spoonscore.domain.Location;
import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.locator.LocationService;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by tap on 10/3/15.
 */
@Component("geolocateRestaurantTransformer")
public class GeolocateRestaurantTransformer extends AbstractMessageTransformer {

    private final Logger logger = LoggerFactory.getLogger(GeolocateRestaurantTransformer.class);

    @Autowired
    @Qualifier(value="GoogleLocationService")
    private LocationService locationService;

    private static final String SPACE = "";
    private static final String COMMA = ",";

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        Restaurant restaurant = (Restaurant)muleMessage.getPayload();
        Location location = restaurant.getLocation();
        StringBuilder addressBuilder = new StringBuilder(location.getAddress());
        addressBuilder.append(SPACE).append(location.getCity()).append(COMMA);
        addressBuilder.append(SPACE).append(location.getState()).append(COMMA);
        addressBuilder.append(SPACE).append(location.getZipCode());
        Location newLocation =  locationService.getLocationByAddress(addressBuilder.toString());
        location.setLongitude(newLocation.getLongitude());
        location.setLatitude(newLocation.getLatitude());
        return restaurant;
    }
}
