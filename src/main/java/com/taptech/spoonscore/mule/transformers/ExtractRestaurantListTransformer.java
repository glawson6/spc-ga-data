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

import java.util.List;
import java.util.Map;

/**
 * Created by tap on 10/3/15.
 */
@Component("extractRestaurantListTransformer")
public class ExtractRestaurantListTransformer extends AbstractMessageTransformer {

    private final Logger logger = LoggerFactory.getLogger(ExtractRestaurantListTransformer.class);

    @Autowired
    private InspectionDataService inspectionDataService;

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        String url = (String)muleMessage.getPayload();
       /* try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            logger.error("Error pausing thread!",e);
        }*/
        Map<String, Object> metaData = inspectionDataService.extractRestaurantData(url);
        List<Restaurant> restaurants = (List<Restaurant>)metaData.get(InspectionDataService.RESTAURANT_KEY);
        Integer count = (Integer)metaData.get(InspectionDataService.NOFR_KEY);
        muleMessage.setInvocationProperty(InspectionDataService.NOFR_KEY,restaurants.size());
        muleMessage.setInvocationProperty("COUNT",count);
        muleMessage.setInvocationProperty(InspectionDataService.URL_KEY,metaData.get(InspectionDataService.URL_KEY));
        return restaurants;
    }
}
