package com.guardedbox.dto;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_PRIVATE_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.ENCRYPTED_VALUE_PATTERN;
import static com.guardedbox.constants.Constraints.PUBLIC_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.SECURITY_QUESTION_MAX_LENGTH;
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

    /** Entropy Expander. */
    @JsonIgnore
    private String entropyExpander;

    /** Public Key. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = PUBLIC_KEY_LENGTH, max = PUBLIC_KEY_LENGTH)
    private String publicKey;

    /** Security Questions. */
    @NotEmpty
    @Size(min = N_SECURITY_QUESTIONS, max = N_SECURITY_QUESTIONS)
    private List<@NotBlank @Size(max = SECURITY_QUESTION_MAX_LENGTH) String> securityQuestions;

    /** Encrypted Private Key. */
    @NotBlank
    @Pattern(regexp = ENCRYPTED_VALUE_PATTERN)
    @Size(min = ENCRYPTED_PRIVATE_KEY_LENGTH, max = ENCRYPTED_PRIVATE_KEY_LENGTH)
    private String encryptedPrivateKey;

    /** Public Key from Security Answers. */
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = PUBLIC_KEY_LENGTH, max = PUBLIC_KEY_LENGTH)
    private String publicKeyFromSecurityAnswers;

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
     * @return The entropyExpander.
     */
    public String getEntropyExpander() {
        return entropyExpander;
    }

    /**
     * @param entropyExpander The entropyExpander to set.
     */
    public void setEntropyExpander(
            String entropyExpander) {
        this.entropyExpander = entropyExpander;
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

    /**
     * @return The publicKeyFromSecurityAnswers.
     */
    public String getPublicKeyFromSecurityAnswers() {
        return publicKeyFromSecurityAnswers;
    }

    /**
     * @param publicKeyFromSecurityAnswers The publicKeyFromSecurityAnswers to set.
     */
    public void setPublicKeyFromSecurityAnswers(
            String publicKeyFromSecurityAnswers) {
        this.publicKeyFromSecurityAnswers = publicKeyFromSecurityAnswers;
    }

}
