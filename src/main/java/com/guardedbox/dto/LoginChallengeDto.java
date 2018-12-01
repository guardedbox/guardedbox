package com.guardedbox.dto;

/**
 * DTO: Response to the get login challenge request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class LoginChallengeDto {

    /** Challenge. */
    private String challenge;

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

}
