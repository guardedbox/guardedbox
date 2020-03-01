package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTION_PUBLIC_KEY_LENGTH;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: Account.
 * Contains the following fields: accountId, email, encryptionPublicKey.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountWithEncryptionPublicKeyEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -7091538371782098518L;

    /** Account ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "account_id")
    private UUID accountId;

    /** Email. */
    @Column(name = "email")
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String email;

    /** Encryption Public Key. */
    @Column(name = "encryption_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = ENCRYPTION_PUBLIC_KEY_LENGTH, max = ENCRYPTION_PUBLIC_KEY_LENGTH)
    private String encryptionPublicKey;

}
