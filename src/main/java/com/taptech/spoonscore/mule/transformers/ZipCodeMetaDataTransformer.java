package com.taptech.spoonscore.mule.transformers;

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
@Component("zipCodeMetaDataTransformer")
public class ZipCodeMetaDataTransformer extends AbstractMessageTransformer {

    private final Logger logger = LoggerFactory.getLogger(ZipCodeMetaDataTransformer.class);

    @Autowired
    private InspectionDataService inspectionDataService;

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {
        String zipCodeStr = (String)muleMessage.getPayload();
        Map<String, Object> metaData  = inspectionDataService.getZipCodeMetaData(Integer.parseInt(zipCodeStr));
        List<String> urls = (List<String>)metaData.get("URLS");
        muleMessage.setInvocationProperty("USIZE",urls.size());
        return urls;
    }
}
