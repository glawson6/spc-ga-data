package com.taptech.spoonscore.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

/**
 * Created by tap on 3/26/15.
 */
public class SearchUtil {

    public final static SimpleDateFormat INSPECTION_DATE_FORMATTER = new SimpleDateFormat("MM/dd/yyyy");

    public static String createInspectionSearchAddress(String address){
        String [] addressTokens = address.split(" ");
        String firstTwoTokens = addressTokens[0]+" "+addressTokens[1];
        return firstTwoTokens;
    }

    public static String encodeString(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }
}
