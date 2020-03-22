package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_64BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_32BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;

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
@SuppressWarnings("serial")
public class CreateAccountDto
        implements Serializable {

    /** Registration Token. */
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = ALPHANUMERIC_64BYTES_LENGTH, max = ALPHANUMERIC_64BYTES_LENGTH)
    private String registrationToken;

    /** Email. */
    @JsonIgnore
    private String email;

    /** Login Salt. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String loginSalt;

    /** Login Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String loginPublicKey;

    /** Encryption Salt. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String encryptionSalt;

    /** Encryption Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String encryptionPublicKey;

    /** Signing Salt. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String signingSalt;

    /** Signing Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String signingPublicKey;

}
