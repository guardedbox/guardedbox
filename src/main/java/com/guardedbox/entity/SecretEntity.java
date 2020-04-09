package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_44BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_JSON_PATTERN;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.repository.AccountsRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: Secret.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "secret")
@Getter
@Setter
@SuppressWarnings("serial")
public class SecretEntity
        implements Serializable {

    /** Secret ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "secret_id")
    private UUID secretId;

    /** Owner Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_account_id")
    @NotNull
    @Valid
    private AccountEntity ownerAccount;

    /** Value. */
    @Column(name = "value")
    @NotBlank
    @Pattern(regexp = BASE64_JSON_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /** Encrypted Key. */
    @Column(name = "encrypted_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_44BYTES_LENGTH, max = BASE64_44BYTES_LENGTH)
    private String encryptedKey;

    /** Must Rotate Key. */
    @Column(name = "must_rotate_key")
    @NotNull
    private Boolean mustRotateKey;

    /** Was Shared. */
    @Column(name = "was_shared")
    @NotNull
    private Boolean wasShared;

    /** Shared Secrets Based on this Secret. */
    @OneToMany(mappedBy = "secret", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedSecretEntity> sharedSecrets;

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The ownerAccount corresponding to the introduced projection class.
     */
    @Transient
    public <T extends AccountBaseProjection> T getOwnerAccount(
            Class<T> type) {

        return AccountsRepository.getProjection(this.getOwnerAccount(), type);

    }

}
