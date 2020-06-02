package com.guardedbox.entity;

import static com.guardedbox.constants.Constraints.EMAIL_MAX_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_MIN_LENGTH;
import static com.guardedbox.constants.Constraints.EMAIL_PATTERN;

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
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.SecretBaseProjection;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.SecretsRepository;

import lombok.Getter;
import lombok.Setter;

/**
 * Entity: InvitationPendingAction.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Entity
@Table(name = "invitation_pending_action")
@Getter
@Setter
@SuppressWarnings("serial")
public class InvitationPendingActionEntity
        implements Serializable {

    /** Invitation Pending Action ID. */
    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(name = "invitation_pending_action_id")
    private UUID invitationPendingActionId;

    /** From Account. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_account_id")
    @NotNull
    @Valid
    private AccountEntity fromAccount;

    /** Receiver Email. */
    @Column(name = "receiver_email")
    @NotBlank
    @Email(regexp = EMAIL_PATTERN)
    @Size(min = EMAIL_MIN_LENGTH, max = EMAIL_MAX_LENGTH)
    private String receiverEmail;

    /** Secret. */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "secret_id")
    @Valid
    private SecretEntity secret;

    /** Group. */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "group_id")
    @Valid
    private GroupEntity group;

    /**
     * @param <T> A projection type.
     * @param type The class of the projection.
     * @return The fromAccount corresponding to the introduced projection class.
     */
    @Transient
    public <T extends AccountBaseProjection> T getFromAccount(
            Class<T> type) {

        return AccountsRepository.getProjection(this.getFromAccount(), type);

    }

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

}
