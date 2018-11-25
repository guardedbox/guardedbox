package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.SecurityParameters.REGISTRATION_TOKEN_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: RegistrationToken.
 * 
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "registration_token")
public class RegistrationTokenEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 3592027573303303253L;

    /** Registration Token ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "registration_token_id")
    @Positive
    private Long registrationTokenId;

    /** Email. */
    @Column(name = "email")
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Token. */
    @Column(name = "token")
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = REGISTRATION_TOKEN_LENGTH, max = REGISTRATION_TOKEN_LENGTH)
    private String token;

    /** Expedition Time. */
    @Column(name = "expedition_time")
    @NotNull
    private Timestamp expeditionTime;

    /**
     * @return The registrationTokenId.
     */
    public Long getRegistrationTokenId() {
        return registrationTokenId;
    }

    /**
     * @param registrationTokenId The registrationTokenId to set.
     */
    public void setRegistrationTokenId(
            Long registrationTokenId) {
        this.registrationTokenId = registrationTokenId;
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
     * @return The token.
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token to set.
     */
    public void setToken(
            String token) {
        this.token = token;
    }

    /**
     * @return The expeditionTime.
     */
    public Timestamp getExpeditionTime() {
        return expeditionTime;
    }

    /**
     * @param expeditionTime The expeditionTime to set.
     */
    public void setExpeditionTime(
            Timestamp expeditionTime) {
        this.expeditionTime = expeditionTime;
    }

}
