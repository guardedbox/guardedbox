package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.InvitationPendingActionEntity;

/**
 * Repository: InvitationPendingAction.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface InvitationPendingActionsRepository
        extends JpaRepository<InvitationPendingActionEntity, UUID>,
        JpaSpecificationExecutor<InvitationPendingActionEntity> {

    /**
     * @param accountId Account.accountId.
     * @param receiverEmail Receiver email.
     * @param secretId Secret.secretId.
     * @return Boolean indicating if an InvitationPendingAction corresponding to any of the introduced accountId, receiverEmail and secretId exists.
     */
    boolean existsByFromAccountAccountIdAndReceiverEmailAndSecretSecretId(
            UUID accountId,
            String receiverEmail,
            UUID secretId);

    /**
     * @param accountId Account.accountId.
     * @param receiverEmail Receiver email.
     * @param groupId Group.groupId.
     * @return Boolean indicating if an InvitationPendingAction corresponding to any of the introduced accountId, receiverEmail and groupId exists.
     */
    boolean existsByFromAccountAccountIdAndReceiverEmailAndGroupGroupId(
            UUID accountId,
            String receiverEmail,
            UUID groupId);

    /**
     * @param receiverEmail InvitationPendingAction.receiverEmail.
     * @return The List of InvitationPendingActionEntities corresponding to the introduced receiverEmail.
     */
    List<InvitationPendingActionEntity> findByReceiverEmail(
            String receiverEmail);

    /**
     * @param secretId Secret.secretId.
     * @return The List of InvitationPendingActionEntities corresponding to the introduced secretId.
     */
    List<InvitationPendingActionEntity> findBySecretSecretId(
            UUID secretId);

    /**
     * @param groupId Group.groupId.
     * @return The List of InvitationPendingActionEntities corresponding to the introduced groupId.
     */
    List<InvitationPendingActionEntity> findByGroupGroupId(
            UUID groupId);

    /**
     * Deletes all InvitationPendingActionEntities by secretId and receiverEmail.
     *
     * @param secretId InvitationPendingAction.secretId.
     * @param receiverEmail InvitationPendingAction.receiverEmail.
     */
    void deleteBySecretSecretIdAndReceiverEmail(
            UUID secretId,
            String receiverEmail);

    /**
     * Deletes all InvitationPendingActionEntities by groupId and receiverEmail.
     *
     * @param groupId InvitationPendingAction.groupId.
     * @param receiverEmail InvitationPendingAction.receiverEmail.
     */
    void deleteByGroupGroupIdAndReceiverEmail(
            UUID groupId,
            String receiverEmail);

}
