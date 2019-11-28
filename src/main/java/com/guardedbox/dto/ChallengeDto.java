package com.guardedbox.dto;

import java.io.Serializable;

/**
 * DTO: Challenge.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class ChallengeDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -347763538667651558L;

    /** Challenge. */
    private String challenge;

    /** Expiration Time. */
    private Long expirationTime;

    /**
     * @return The challenge.
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * @param challenge The challenge to set.
     */
    public void setChallenge(
            String challenge) {
        this.challenge = challenge;
    }

    /**
     * @return The expirationTime.
     */
    public Long getExpirationTime() {
        return expirationTime;
    }

    /**
     * @param expirationTime The expirationTime to set.
     */
    public void setExpirationTime(
            Long expirationTime) {
        this.expirationTime = expirationTime;
    }

}
