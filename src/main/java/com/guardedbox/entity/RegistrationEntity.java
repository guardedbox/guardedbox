package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.REGISTRATION_TOKEN_LENGTH;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: Registration.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "registration")
@Getter
@Setter
public class RegistrationEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 3592027573303303253L;

    /** Registration ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "registration_id")
    private UUID registrationId;

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

}
