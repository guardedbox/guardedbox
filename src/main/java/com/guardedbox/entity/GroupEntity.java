package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.BASE64_44BYTES_LENGTH;
import static com.guardedbox.constants.Constraints.BASE64_PATTERN;
import static com.guardedbox.constants.Constraints.GROUP_NAME_MAX_LENGTH;

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
 * Entity: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "group_")
@Getter
@Setter
@SuppressWarnings("serial")
public class GroupEntity
        implements Serializable {

    /** Group ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "group_id")
    private UUID groupId;

    /** Owner Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_account_id")
    @NotNull
    @Valid
    private AccountEntity ownerAccount;

    /** Name. */
    @Column(name = "name")
    @NotBlank
    @Size(max = GROUP_NAME_MAX_LENGTH)
    private String name;

    /** Encrypted Group Key. */
    @Column(name = "encrypted_group_key")
    @NotBlank
    @Pattern(regexp = BASE64_PATTERN)
    @Size(min = BASE64_44BYTES_LENGTH, max = BASE64_44BYTES_LENGTH)
    private String encryptedGroupKey;

    /** Participants. */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupParticipantEntity> participants;

    /** Secrets. */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupSecretEntity> secrets;

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
