package com.taptech.spoonscore.mule.transformers;

import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.locator.LocationService;
import com.taptech.spoonscore.service.InspectionDataService;
import com.taptech.spoonscore.service.RestaurantCombineUpdate;
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
@Component("combineRestaurantDataTransformer")
public class CombineRestaurantDataTransformer extends AbstractMessageTransformer {

    private final Logger logger = LoggerFactory.getLogger(CombineRestaurantDataTransformer.class);

    @Autowired
    private InspectionDataService inspectionDataService;

    @Autowired
    @Qualifier(value="yelpRestaurantUpdate")
    private RestaurantCombineUpdate yelpCombineUpdate;

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        Restaurant restaurant = (Restaurant)muleMessage.getPayload();
        yelpCombineUpdate.combineRestaurantData(restaurant);
        return restaurant;
    }
}
