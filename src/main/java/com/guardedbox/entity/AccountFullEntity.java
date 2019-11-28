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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: Account.
 * Contains all the fields.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "account")
public class AccountFullEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 4171368265398710978L;

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

    /** Salt. */
    @Column(name = "salt")
    @NotNull
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

    /**
     * @return The accountId.
     */
    public Long getAccountId() {
        return accountId;
    }

    /**
     * @param accountId The accountId to set.
     */
    public void setAccountId(
            Long accountId) {
        this.accountId = accountId;
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

    /**
     * @return The ownedSecrets.
     */
    public List<SecretEntity> getOwnedSecrets() {
        return ownedSecrets;
    }

    /**
     * @param ownedSecrets The ownedSecrets to set.
     */
    public void setOwnedSecrets(
            List<SecretEntity> ownedSecrets) {
        this.ownedSecrets = ownedSecrets;
    }

    /**
     * @return The receivedSharedSecrets.
     */
    public List<SharedSecretEntity> getReceivedSharedSecrets() {
        return receivedSharedSecrets;
    }

    /**
     * @param receivedSharedSecrets The receivedSharedSecrets to set.
     */
    public void setReceivedSharedSecrets(
            List<SharedSecretEntity> receivedSharedSecrets) {
        this.receivedSharedSecrets = receivedSharedSecrets;
    }

}
