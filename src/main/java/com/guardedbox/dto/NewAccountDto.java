package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_PRIVATE_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.ENCRYPTED_VALUE_PATTERN;
import static com.guardedbox.constants.Constraints.HEX_PATTERN;
import static com.guardedbox.constants.Constraints.PUBLIC_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.SECURITY_QUESTION_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.SHA512_HEX_LENGTH;
import static com.guardedbox.constants.SecurityParameters.N_SECURITY_QUESTIONS;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * DTO: Body of the register account request.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public class NewAccountDto {

    /** Registration Token. */
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH)
    private String registrationToken;

    /** Email. */
    @JsonIgnore
    private String email;

    /** Password. */
    @NotBlank
    @Pattern(regexp = HEX_PATTERN)
    @Size(min = SHA512_HEX_LENGTH, max = SHA512_HEX_LENGTH)
    private String password;

    /** Security Questions. */
    @NotEmpty
    @Size(min = N_SECURITY_QUESTIONS, max = N_SECURITY_QUESTIONS)
    private List<@NotBlank @Size(max = SECURITY_QUESTION_MAX_LENGTH) String> securityQuestions;

    /** Security Answers. */
    @NotBlank
    @Pattern(regexp = HEX_PATTERN)
    @Size(min = SHA512_HEX_LENGTH, max = SHA512_HEX_LENGTH)
    private String securityAnswers;

    /** Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = PUBLIC_KEY_LENGTH, max = PUBLIC_KEY_LENGTH)
    private String publicKey;

    /** Encrypted Private Key. */
    @NotBlank
    @Pattern(regexp = ENCRYPTED_VALUE_PATTERN)
    @Size(min = ENCRYPTED_PRIVATE_KEY_LENGTH, max = ENCRYPTED_PRIVATE_KEY_LENGTH)
    private String encryptedPrivateKey;

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
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password The password to set.
     */
    public void setPassword(
            String password) {
        this.password = password;
    }

    /**
     * @return The securityQuestions.
     */
    public List<String> getSecurityQuestions() {
        return securityQuestions;
    }

    /**
     * @param securityQuestions The securityQuestions to set.
     */
    public void setSecurityQuestions(
            List<String> securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    /**
     * @return The securityAnswers.
     */
    public String getSecurityAnswers() {
        return securityAnswers;
    }

    /**
     * @param securityAnswers The securityAnswers to set.
     */
    public void setSecurityAnswers(
            String securityAnswers) {
        this.securityAnswers = securityAnswers;
    }

    /**
     * @return The publicKey.
     */
    public String getPublicKey() {
        return publicKey;
    }

    /**
     * @param publicKey The publicKey to set.
     */
    public void setPublicKey(
            String publicKey) {
        this.publicKey = publicKey;
    }

    /**
     * @return The encryptedPrivateKey.
     */
    public String getEncryptedPrivateKey() {
        return encryptedPrivateKey;
    }

    /**
     * @param encryptedPrivateKey The encryptedPrivateKey to set.
     */
    public void setEncryptedPrivateKey(
            String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }

}
