package com.taptech.spoonscore.service;

/**
 * Created by tap on 10/3/15.
 */
public class DataExtractionExcepton extends RuntimeException {
    public DataExtractionExcepton() {
    }

    public DataExtractionExcepton(String message) {
        super(message);
    }

    public DataExtractionExcepton(String message, Throwable cause) {
        super(message, cause);
    }

    public DataExtractionExcepton(Throwable cause) {
        super(cause);
    }

    public DataExtractionExcepton(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
