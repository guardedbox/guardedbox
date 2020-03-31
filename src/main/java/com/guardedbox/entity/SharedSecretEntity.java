package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_44BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.SecretBaseProjection;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.SecretsRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: SharedSecret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "shared_secret")
@Getter
@Setter
@SuppressWarnings("serial")
public class SharedSecretEntity
        implements Serializable {

    /** Shared Secret ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "shared_secret_id")
    private UUID sharedSecretId;

    /** Secret. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "secret_id")
    @NotNull
    @Valid
    private SecretEntity secret;

    /** Receiver Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_account_id")
    @NotNull
    @Valid
    private AccountEntity receiverAccount;

    /** Encrypted Key. */
    @Column(name = "encrypted_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_44BYTES_LENGTH, max = BASE64_44BYTES_LENGTH)
    private String encryptedKey;

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The secret corresponding to the introduced projection class.
     */
    @Transient
    public <T extends SecretBaseProjection> T getSecret(
            Class<T> type) {

        return SecretsRepository.getProjection(this.getSecret(), type);

    }

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The receiverAccount corresponding to the introduced projection class.
     */
    @Transient
    public <T extends AccountBaseProjection> T getReceiverAccount(
            Class<T> type) {

        return AccountsRepository.getProjection(this.getReceiverAccount(), type);

    }

}
