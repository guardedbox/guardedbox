package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_KEY_LENGTH;
import static com.guardedbox.constants.Constraints.GROUP_NAME_MAX_LENGTH;

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

/**
 * Entity: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "group")
public class GroupEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -7611810897991586085L;

    /** Group ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    @Positive
    private Long groupId;

    /** Owner Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_account_id")
    @NotNull
    @Valid
    private AccountWithEncryptionPublicKeyEntity ownerAccount;

    /** Name. */
    @Column(name = "name")
    @NotBlank
    @Size(max = GROUP_NAME_MAX_LENGTH)
    private String name;

    /** Encrypted Group Key. */
    @Column(name = "encrypted_group_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = ENCRYPTED_KEY_LENGTH)
    private String encryptedGroupKey;

    /** Participants. */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupParticipantEntity> participants;

    /** Secrets. */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupSecretEntity> secrets;

    /**
     * @return The groupId.
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The groupId to set.
     */
    public void setGroupId(
            Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return The ownerAccount.
     */
    public AccountWithEncryptionPublicKeyEntity getOwnerAccount() {
        return ownerAccount;
    }

    /**
     * @param ownerAccount The ownerAccount to set.
     */
    public void setOwnerAccount(
            AccountWithEncryptionPublicKeyEntity ownerAccount) {
        this.ownerAccount = ownerAccount;
    }

    /**
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(
            String name) {
        this.name = name;
    }

    /**
     * @return The encryptedGroupKey.
     */
    public String getEncryptedGroupKey() {
        return encryptedGroupKey;
    }

    /**
     * @param encryptedGroupKey The encryptedGroupKey to set.
     */
    public void setEncryptedGroupKey(
            String encryptedGroupKey) {
        this.encryptedGroupKey = encryptedGroupKey;
    }

    /**
     * @return The participants.
     */
    public List<GroupParticipantEntity> getParticipants() {
        return participants;
    }

    /**
     * @param participants The participants to set.
     */
    public void setParticipants(
            List<GroupParticipantEntity> participants) {
        this.participants = participants;
    }

    /**
     * @return The secrets.
     */
    public List<GroupSecretEntity> getSecrets() {
        return secrets;
    }

    /**
     * @param secrets The secrets to set.
     */
    public void setSecrets(
            List<GroupSecretEntity> secrets) {
        this.secrets = secrets;
    }

}
