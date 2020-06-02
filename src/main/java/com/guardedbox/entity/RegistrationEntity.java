package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.ALPHANUMERIC_64BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.ALPHANUMERIC_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
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
@SuppressWarnings("serial")
public class RegistrationEntity
        implements Serializable {

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

    /** From Email. */
    @Column(name = "from_email")
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String fromEmail;

    /** Token. */
    @Column(name = "token")
    @NotBlank
    @Pattern(regexp = ALPHANUMERIC_PATTERN)
    @Size(min = ALPHANUMERIC_64BYTES_LENGTH, max = ALPHANUMERIC_64BYTES_LENGTH)
    private String token;

    /** Creation Time. */
    @Column(name = "creation_time")
    @NotNull
    private Timestamp creationTime;

    /** Valid. */
    @Column(name = "valid")
    @NotNull
    private Boolean valid;

    /** Consumed. */
    @Column(name = "consumed")
    @NotNull
    private Boolean consumed;

    /** Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "account_id")
    @Valid
    private AccountEntity account;

}
