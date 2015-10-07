package com.taptech.spoonscore.mule.transformers;

import com.taptech.spoonscore.domain.Restaurant;
import com.taptech.spoonscore.service.InspectionDataService;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by tap on 10/3/15.
 */
@Component("persistRestaurantStageTransformer")
public class PersistRestaurantStageTransformer extends AbstractMessageTransformer {

    private final Logger logger = LoggerFactory.getLogger(PersistRestaurantStageTransformer.class);

    @Autowired
    private InspectionDataService inspectionDataService;

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        Restaurant restaurant = (Restaurant)muleMessage.getPayload();
        inspectionDataService.saveRestaurantInStageDatabase(restaurant);
        return restaurant;
    }
}
