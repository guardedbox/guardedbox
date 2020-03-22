package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_32BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: Account.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
@Getter
@Setter
@SuppressWarnings("serial")
public class AccountEntity
        implements Serializable {

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

    /** Login Salt. */
    @Column(name = "login_salt")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String loginSalt;

    /** Login Public Key. */
    @Column(name = "login_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String loginPublicKey;

    /** Encryption Salt. */
    @Column(name = "encryption_salt")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String encryptionSalt;

    /** Encryption Public Key. */
    @Column(name = "encryption_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String encryptionPublicKey;

    /** Signing Salt. */
    @Column(name = "signing_salt")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String signingSalt;

    /** Signing Public Key. */
    @Column(name = "signing_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_32BYTES_LENGTH, max = BASE64_32BYTES_LENGTH)
    private String signingPublicKey;

    /** Secrets. */
    @OneToMany(mappedBy = "ownerAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretEntity> ownedSecrets;

    /** Secrets Shared with this Account. */
    @OneToMany(mappedBy = "receiverAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedSecretEntity> receivedSharedSecrets;

    /** Owned Groups. */
    @OneToMany(mappedBy = "ownerAccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupEntity> ownedGroups;

    /** Group Participations. */
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupParticipantEntity> groupParticipations;

}
