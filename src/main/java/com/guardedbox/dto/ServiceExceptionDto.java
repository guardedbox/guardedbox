package com.guardedbox.dto;

import java.util.Map;

/**
 * DTO: Response to requests that have thrown a ServiceException.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class ServiceExceptionDto {

    /** Error Code. */
    private String errorCode;

    /** Additional Data. */
    private Map<String, Object> additionalData;

    /**
     * @return The errorCode.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode The errorCode to set.
     */
    public void setErrorCode(
            String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return The additionalData.
     */
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    /**
     * @param additionalData The additionalData to set.
     */
    public void setAdditionalData(
            Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

}
