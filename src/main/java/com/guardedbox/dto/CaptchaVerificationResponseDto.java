package com.guardedbox.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO: Response to the captcha verification request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class CaptchaVerificationResponseDto {

    /** Indicates if the verification is successful. */
    @JsonProperty("success")
    private Boolean success;

    /** Challenge timestamp. */
    @JsonProperty("challenge_ts")
    private String challengeTimestamp;

    /** Hostname of the site where the reCAPTCHA was solved. */
    @JsonProperty("hostname")
    private String hostname;

    /** Error codes. */
    @JsonProperty("error-codes")
    private List<String> errorCodes;

    /**
     * @return The success.
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * @param success The success to set.
     */
    public void setSuccess(
            Boolean success) {
        this.success = success;
    }

    /**
     * @return The challengeTimestamp.
     */
    public String getChallengeTimestamp() {
        return challengeTimestamp;
    }

    /**
     * @param challengeTimestamp The challengeTimestamp to set.
     */
    public void setChallengeTimestamp(
            String challengeTimestamp) {
        this.challengeTimestamp = challengeTimestamp;
    }

    /**
     * @return The hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname The hostname to set.
     */
    public void setHostname(
            String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return The errorCodes.
     */
    public List<String> getErrorCodes() {
        return errorCodes;
    }

    /**
     * @param errorCodes The errorCodes to set.
     */
    public void setErrorCodes(
            List<String> errorCodes) {
        this.errorCodes = errorCodes;
    }

}
