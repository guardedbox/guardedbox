package com.guardedbox.dto;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: One time password response.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
@SuppressWarnings("serial")
public class OtpResponseDto
        implements Serializable {

    /** One time password. */
    @NotBlank
    private String otp;

}
