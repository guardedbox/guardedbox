package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.SecurityParameters.OTP_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

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
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = 1, max = OTP_LENGTH)
    private String otp;

}
