package com.guardedbox.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: One time password.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
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

}
