package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.SIGNING_PUBLIC_KEY_LENGTH;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: Account.
 * Contains the following fields: accountId, email, signingPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountWithSigningPublicKeyEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3866191874226530489L;

    /** Account ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    @Positive
    private Long accountId;

    /** Email. */
    @Column(name = "email")
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Signing Public Key. */
    @Column(name = "signing_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = SIGNING_PUBLIC_KEY_LENGTH, max = SIGNING_PUBLIC_KEY_LENGTH)
    private String signingPublicKey;

}
