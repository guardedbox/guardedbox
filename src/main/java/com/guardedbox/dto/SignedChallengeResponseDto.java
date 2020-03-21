package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.BASE64_64BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Signed Challenge Response.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class SignedChallengeResponseDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -1634157359106978079L;

    /** Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Signed Challenge Response. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_64BYTES_LENGTH, max = BASE64_64BYTES_LENGTH)
    private String signedChallengeResponse;

}
