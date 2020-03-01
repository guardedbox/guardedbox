package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.ENCRYPTED_KEY_LENGTH;

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
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: GroupParticipant.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "group_participant")
@Getter
@Setter
public class GroupParticipantEntity
        implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3662560674824178640L;

    /** Group ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "group_participant_id")
    private UUID groupParticipantId;

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
    @Size(min = ENCRYPTED_KEY_LENGTH, max = ENCRYPTED_KEY_LENGTH)
    private String encryptedGroupKey;

}
