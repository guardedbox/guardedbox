package com.guardedbox.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class RegistrationDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 998073884156272898L;

    /** Registration ID. */
    @JsonIgnore
    private Long registrationId;

    /** Email. */
    private String email;

    /** Token. */
    private String token;

    /** Expedition Time. */
    @JsonIgnore
    private Timestamp expeditionTime;

    /**
     * @return The registrationId.
     */
    public Long getRegistrationId() {
        return registrationId;
    }

    /**
     * @param registrationId The registrationId to set.
     */
    public void setRegistrationId(
            Long registrationId) {
        this.registrationId = registrationId;
    }

    /**
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(
            String email) {
        this.email = email;
    }

    /**
     * @return The token.
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token to set.
     */
    public void setToken(
            String token) {
        this.token = token;
    }

    /**
     * @return The expeditionTime.
     */
    public Timestamp getExpeditionTime() {
        return expeditionTime;
    }

    /**
     * @param expeditionTime The expeditionTime to set.
     */
    public void setExpeditionTime(
            Timestamp expeditionTime) {
        this.expeditionTime = expeditionTime;
    }

}
