package com.guardedbox.dto;

import java.io.Serializable;

/**
 * DTO: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class SecretDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3608188899441682632L;

    /** Secret ID. */
    private Long secretId;

    /** Name. */
    private String name;

    /** Value. */
    private String value;

    /**
     * @return The secretId.
     */
    public Long getSecretId() {
        return secretId;
    }

    /**
     * @param secretId The secretId to set.
     */
    public void setSecretId(
            Long secretId) {
        this.secretId = secretId;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(
            String name) {
        this.name = name;
    }

    /**
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(
            String value) {
        this.value = value;
    }

}
