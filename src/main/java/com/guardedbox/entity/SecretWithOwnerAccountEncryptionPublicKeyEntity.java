package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.SECRET_NAME_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.SECRET_VALUE_MAX_LENGTH;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

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
public class SecretWithOwnerAccountEncryptionPublicKeyEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = 6161942842947773439L;

    /** Secret ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "secret_id")
    @Positive
    private Long secretId;

    /** Owner Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_account_id")
    @NotNull
    @Valid
    private AccountWithEncryptionPublicKeyEntity ownerAccount;

    /** Name. */
    @Column(name = "name")
    @NotBlank
    @Size(max = SECRET_NAME_MAX_LENGTH)
    private String name;

    /** Value. */
    @Column(name = "value")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = SECRET_VALUE_MAX_LENGTH)
    private String value;

    /** Shared Secrets Based on this Secret. */
    @OneToMany(mappedBy = "secret", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SharedSecretEntity> sharedSecrets;

}
