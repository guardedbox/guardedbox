package com.guardedbox.dto;

import java.io.Serializable;

/**
 * DTO: One time password.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class OtpDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -2657701526237333062L;

    /** Email. */
    private String email;

    /** One time password. */
    private String otp;

    /** Expiration Time. */
    private Long expirationTime;

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
     * @return The otp.
     */
    public String getOtp() {
        return otp;
    }

    /**
     * @param otp The otp to set.
     */
    public void setOtp(
            String otp) {
        this.otp = otp;
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
