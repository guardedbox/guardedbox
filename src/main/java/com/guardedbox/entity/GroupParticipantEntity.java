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
import com.guardedbox.repository.AccountsRepository;

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
@SuppressWarnings("serial")
public class GroupParticipantEntity
        implements Serializable {

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
    private AccountEntity account;

    /** Encrypted Group Key. */
    @Column(name = "encrypted_group_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_44BYTES_LENGTH, max = BASE64_44BYTES_LENGTH)
    private String encryptedGroupKey;

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The account corresponding to the introduced projection class.
     */
    @Transient
    public <T extends AccountBaseProjection> T getAccount(
            Class<T> type) {

        return AccountsRepository.getProjection(this.getAccount(), type);

    }

}
