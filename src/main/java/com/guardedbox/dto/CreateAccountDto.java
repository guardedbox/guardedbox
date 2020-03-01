package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTION_PUBLIC_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.SALT_LENGTH;
import static com.guardedbox.constants.Constraints.SIGNING_PUBLIC_KEY_LENGTH;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO: Body of the create account request.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Getter
@Setter
public class CreateAccountDto
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -2716784112428481474L;

    /** Registration Token. */
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH)
    private String registrationToken;

    /** Email. */
    @JsonIgnore
    private String email;

    /** Salt. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = SALT_LENGTH, max = SALT_LENGTH)
    private String salt;

    /** Encryption Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = ENCRYPTION_PUBLIC_KEY_LENGTH, max = ENCRYPTION_PUBLIC_KEY_LENGTH)
    private String encryptionPublicKey;

    /** Signing Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = SIGNING_PUBLIC_KEY_LENGTH, max = SIGNING_PUBLIC_KEY_LENGTH)
    private String signingPublicKey;

}
