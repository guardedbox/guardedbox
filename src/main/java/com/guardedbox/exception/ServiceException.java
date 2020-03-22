package com.guardedbox.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Runtime exception thrown by services.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("serial")
public class ServiceException
        extends RuntimeException {

    /** Error Code. */
    private String errorCode;

    /** Additional Data. */
    private Map<String, Object> additionalData;

    /** Response As Success. */
    private Boolean responseAsSuccess;

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

}
