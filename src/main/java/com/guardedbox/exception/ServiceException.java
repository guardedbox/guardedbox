package com.guardedbox.exception;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Runtime exception thrown by services.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class ServiceException
        extends RuntimeException {

    /** Serial Version UID. */
    private static final long serialVersionUID = -1983998422755009837L;

    /** Error Code. */
    private String errorCode;

    /** Additional Data. */
    private Map<String, Object> additionalData;

    /** Response As Success. */
    private Boolean responseAsSuccess;

    /**
     * Default constructor.
     */
    public ServiceException() {
        super();
    }

    /**
     * Constructor with message.
     *
     * @param message The message.
     */
    public ServiceException(
            String message) {
        super(message);
    }

    /**
     * Constructor with cause.
     *
     * @param cause The cause.
     */
    public ServiceException(
            Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message The message.
     * @param cause The cause.
     */
    public ServiceException(
            String message,
            Throwable cause) {
        super(message, cause);
    }

    /**
     * @return The errorCode.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode The errorCode to set.
     * @return This ServiceException.
     */
    public ServiceException setErrorCode(
            String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * @return The additionalData.
     */
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    /**
     * @param additionalData The additionalData to set.
     * @return This ServiceException.
     */
    public ServiceException setAdditionalData(
            Map<String, Object> additionalData) {
        this.additionalData = additionalData;
        return this;
    }

    /**
     * Adds a name-value pair to the additional data.
     *
     * @param name The name.
     * @param value The value.
     * @return This ServiceException.
     */
    public ServiceException addAdditionalData(
            String name,
            Object value) {
        if (this.additionalData == null)
            this.additionalData = new LinkedHashMap<String, Object>();
        this.additionalData.put(name, value);
        return this;
    }

    /**
     * Adds a name-value pairs collection to the additional data.
     *
     * @param additionalData The name-value pairs collection.
     * @return This ServiceException.
     */
    public ServiceException addAdditionalData(
            Map<String, Object> additionalData) {
        if (this.additionalData == null)
            this.additionalData = new LinkedHashMap<String, Object>();
        this.additionalData.putAll(additionalData);
        return this;
    }

    /**
     * @return The responseAsSuccess.
     */
    public Boolean getResponseAsSuccess() {
        return responseAsSuccess;
    }

    /**
     * @param responseAsSuccess the responseAsSuccess to set.
     * @return This ServiceException.
     */
    public ServiceException setResponseAsSuccess(
            Boolean responseAsSuccess) {
        this.responseAsSuccess = responseAsSuccess;
        return this;
    }

}
