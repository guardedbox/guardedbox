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

/**
 * DTO: Body of the create account request.
 *
 * @author s3curitybug@gmail.com
 *
 */
public class CreateAccountDto
        extends MinedChallengeResponseDto
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

    /**
     * @return The registrationToken.
     */
    public String getRegistrationToken() {
        return registrationToken;
    }

    /**
     * @param registrationToken The registrationToken to set.
     */
    public void setRegistrationToken(
            String registrationToken) {
        this.registrationToken = registrationToken;
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
     * @return The salt.
     */
    public String getSalt() {
        return salt;
    }

    /**
     * @param salt The salt to set.
     */
    public void setSalt(
            String salt) {
        this.salt = salt;
    }

    /**
     * @return The encryptionPublicKey.
     */
    public String getEncryptionPublicKey() {
        return encryptionPublicKey;
    }

    /**
     * @param encryptionPublicKey The encryptionPublicKey to set.
     */
    public void setEncryptionPublicKey(
            String encryptionPublicKey) {
        this.encryptionPublicKey = encryptionPublicKey;
    }

    /**
     * @return The signingPublicKey.
     */
    public String getSigningPublicKey() {
        return signingPublicKey;
    }

    /**
     * @param signingPublicKey The signingPublicKey to set.
     */
    public void setSigningPublicKey(
            String signingPublicKey) {
        this.signingPublicKey = signingPublicKey;
    }

}
