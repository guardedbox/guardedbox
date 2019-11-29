package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_KEY_LENGTH;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * Entity: GroupParticipant.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "group_participant")
public class GroupParticipantEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3662560674824178640L;

    /** Group ID. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_participant_id")
    @Positive
    private Long groupParticipantId;

    /** Group. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id")
    @NotNull
    @Valid
    private GroupEntity group;

    /** Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id")
    @NotNull
    @Valid
    private AccountWithEncryptionPublicKeyEntity account;

    /** Encrypted Group Key. */
    @Column(name = "encrypted_group_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(max = ENCRYPTED_KEY_LENGTH)
    private String encryptedGroupKey;

    /**
     * @return The groupParticipantId.
     */
    public Long getGroupParticipantId() {
        return groupParticipantId;
    }

    /**
     * @param groupParticipantId The groupParticipantId to set.
     */
    public void setGroupParticipantId(
            Long groupParticipantId) {
        this.groupParticipantId = groupParticipantId;
    }

    /**
     * @return The group.
     */
    public GroupEntity getGroup() {
        return group;
    }

    /**
     * @param group The group to set.
     */
    public void setGroup(
            GroupEntity group) {
        this.group = group;
    }

    /**
     * @return The account.
     */
    public AccountWithEncryptionPublicKeyEntity getAccount() {
        return account;
    }

    /**
     * @param account The account to set.
     */
    public void setAccount(
            AccountWithEncryptionPublicKeyEntity account) {
        this.account = account;
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

}
