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
public class OtpResponseDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 7137762692695404652L;

    /** One time password. */
    @NotBlank
    private String otp;

}
