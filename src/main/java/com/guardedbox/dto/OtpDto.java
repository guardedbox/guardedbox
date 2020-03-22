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
@SuppressWarnings("serial")
public class OtpDto
        implements Serializable {

    /** Email. */
    private String email;

    /** One time password. */
    private String otp;

    /** Expiration Time. */
    private Long expirationTime;

}
