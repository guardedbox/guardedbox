package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.HEX_PATTERN;
import static com.guardedbox.constants.Constraints.SIGNATURE_MAX_LENGTH;
import static com.guardedbox.constants.SecurityParameters.LOGIN_CODE_LENGTH;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO: Body of the login request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class LoginDto {

    /** Login Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Challenge Response. */
    @NotBlank
    @Pattern(regexp = HEX_PATTERN)
    @Size(max = SIGNATURE_MAX_LENGTH)
    private String challengeResponse;

    /** Login Code. */
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = LOGIN_CODE_LENGTH, max = LOGIN_CODE_LENGTH)
    private String code;

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
     * @return The challengeResponse.
     */
    public String getChallengeResponse() {
        return challengeResponse;
    }

    /**
     * @param challengeResponse The challengeResponse to set.
     */
    public void setChallengeResponse(
            String challengeResponse) {
        this.challengeResponse = challengeResponse;
    }

    /**
     * @return The code.
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code to set.
     */
    public void setCode(
            String code) {
        this.code = code;
    }

}
