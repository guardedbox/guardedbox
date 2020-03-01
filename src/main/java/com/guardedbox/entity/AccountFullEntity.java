package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTION_PUBLIC_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.SALT_LENGTH;
import static com.guardedbox.constants.Constraints.SIGNING_PUBLIC_KEY_LENGTH;

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
 * Contains all the fields.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
@Getter
@Setter
public class AccountFullEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 4171368265398710978L;

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

    /** Salt. */
    @Column(name = "salt")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = SALT_LENGTH, max = SALT_LENGTH)
    private String salt;

    /** Encryption Public Key. */
    @Column(name = "encryption_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = ENCRYPTION_PUBLIC_KEY_LENGTH, max = ENCRYPTION_PUBLIC_KEY_LENGTH)
    private String encryptionPublicKey;

    /** Signing Public Key. */
    @Column(name = "signing_public_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = SIGNING_PUBLIC_KEY_LENGTH, max = SIGNING_PUBLIC_KEY_LENGTH)
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
