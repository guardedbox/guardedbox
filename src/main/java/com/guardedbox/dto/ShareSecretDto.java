package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_VALUE_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * DTO: Body of the share secret request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class ShareSecretDto {

    /** Secret ID. */
    @NotNull
    @Positive
    private Long secretId;

    /** Email. */
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Value. */
    @NotBlank
    @Pattern(regexp = ENCRYPTED_VALUE_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /**
     * @return The secretId.
     */
    public Long getSecretId() {
        return secretId;
    }

    /**
     * @param secretId The secretId to set.
     */
    public void setSecretId(
            Long secretId) {
        this.secretId = secretId;
    }

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
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(
            String value) {
        this.value = value;
    }

}
