package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

/**
 * DTO: RegistrationToken.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class RegistrationTokenDto {

    /** Registration Token ID. */
    @JsonIgnore
    private Long registrationTokenId;

    /** Email. */
    private String email;

    /** Token. */
    private String token;

    /** Expedition Time. */
    @JsonIgnore
    private Timestamp expeditionTime;

    /**
     * @return The registrationTokenId.
     */
    public Long getRegistrationTokenId() {
        return registrationTokenId;
    }

    /**
     * @param registrationTokenId The registrationTokenId to set.
     */
    public void setRegistrationTokenId(
            Long registrationTokenId) {
        this.registrationTokenId = registrationTokenId;
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
