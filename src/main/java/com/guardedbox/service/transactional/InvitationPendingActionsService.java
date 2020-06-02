package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.CreateInvitationPendingActionDto;
import com.guardedbox.dto.InvitationPendingActionDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.GroupEntity;
import com.guardedbox.entity.InvitationPendingActionEntity;
import com.guardedbox.entity.SecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.repository.AccountsRepository;
import com.guardedbox.repository.InvitationPendingActionsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: InvitationPendingAction.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class InvitationPendingActionsService {

    /** InvitationPendingActionsRepository. */
    private final InvitationPendingActionsRepository invitationPendingActionsRepository;

    /** AccountsRepository. */
    private final AccountsRepository accountsRepository;

    /** SecretsService. */
    private final SecretsService secretsService;

    /** GroupsService. */
    private final GroupsService groupsService;

    /**
     * @param ownerAccountId Account.accountId.
     * @param secretId A secret ID.
     * @return The List of InvitationPendingActionDtos corresponding to the introduced secret ID.
     */
    public List<InvitationPendingActionDto> getInvitationPendingActionsBySecretId(
            UUID ownerAccountId,
            UUID secretId) {

        // Check that the secret exists and belongs to the ownerAccountId.
        secretsService.findAndCheckSecret(secretId, ownerAccountId);

        // Find and map all invitation pending actions associated to the secretId.
        List<InvitationPendingActionEntity> invitationPendingActionEntities = invitationPendingActionsRepository.findBySecretSecretId(secretId);
        List<InvitationPendingActionDto> invitationPendingActionDtos = new ArrayList<>(invitationPendingActionEntities.size());

        for (InvitationPendingActionEntity invitationPendingActionEntity : invitationPendingActionEntities) {
            invitationPendingActionDtos.add(new InvitationPendingActionDto()
                    .setInvitationPendingActionId(invitationPendingActionEntity.getInvitationPendingActionId())
                    .setReceiverEmail(invitationPendingActionEntity.getReceiverEmail())
                    .setSecretId(invitationPendingActionEntity.getSecret().getSecretId())
                    .setEmailRegistered(accountsRepository.existsByEmail(invitationPendingActionEntity.getReceiverEmail())));
        }

        return invitationPendingActionDtos;

    }

    /**
     * @param ownerAccountId Account.accountId.
     * @param groupId A group ID.
     * @return The List of InvitationPendingActionDtos corresponding to the introduced group ID.
     */
    public List<InvitationPendingActionDto> getInvitationPendingActionsByGroupId(
            UUID ownerAccountId,
            UUID groupId) {

        // Check that the group exists and belongs to the ownerAccountId.
        groupsService.findAndCheckGroup(groupId, ownerAccountId, false);

        // Find and map all invitation pending actions associated to the groupId.
        List<InvitationPendingActionEntity> invitationPendingActionEntities = invitationPendingActionsRepository.findByGroupGroupId(groupId);
        List<InvitationPendingActionDto> invitationPendingActionDtos = new ArrayList<>(invitationPendingActionEntities.size());

        for (InvitationPendingActionEntity invitationPendingActionEntity : invitationPendingActionEntities) {
            invitationPendingActionDtos.add(new InvitationPendingActionDto()
                    .setInvitationPendingActionId(invitationPendingActionEntity.getInvitationPendingActionId())
                    .setReceiverEmail(invitationPendingActionEntity.getReceiverEmail())
                    .setGroupId(invitationPendingActionEntity.getGroup().getGroupId())
                    .setEmailRegistered(accountsRepository.existsByEmail(invitationPendingActionEntity.getReceiverEmail())));
        }

        return invitationPendingActionDtos;

    }

    /**
     * Creates an InvitationPendingAction.
     *
     * @param createInvitationPendingActionDto Object with the necessary data to create an InvitationPendingAction.
     * @param fromAccount AccountDto representing the inviter.
     */
    public void createInvitationPendingAction(
            CreateInvitationPendingActionDto createInvitationPendingActionDto,
            AccountDto fromAccount) {

        InvitationPendingActionEntity invitationPendingActionEntity = new InvitationPendingActionEntity();

        // From account.
        invitationPendingActionEntity.setFromAccount(new AccountEntity().setAccountId(fromAccount.getAccountId()));

        // Receiver email.
        if (fromAccount.getEmail().equals(createInvitationPendingActionDto.getReceiverEmail())) {
            throw new ServiceException(String.format(
                    "Email %s tryed to create an invitation pending action with itself as receiver email", createInvitationPendingActionDto
                            .getReceiverEmail()));
        }

        invitationPendingActionEntity.setReceiverEmail(createInvitationPendingActionDto.getReceiverEmail());

        // Secret.
        if (createInvitationPendingActionDto.getSecretId() != null) {

            SecretEntity secretEntity = secretsService.findAndCheckSecret(
                    createInvitationPendingActionDto.getSecretId(),
                    fromAccount.getAccountId());

            if (!invitationPendingActionsRepository.existsByFromAccountAccountIdAndReceiverEmailAndSecretSecretId(
                    fromAccount.getAccountId(),
                    createInvitationPendingActionDto.getReceiverEmail(),
                    createInvitationPendingActionDto.getSecretId())) {
                invitationPendingActionEntity.setSecret(secretEntity);
            }

        }

        // Group.
        if (createInvitationPendingActionDto.getGroupId() != null) {

            GroupEntity groupEntity = groupsService.findAndCheckGroup(
                    createInvitationPendingActionDto.getGroupId(),
                    fromAccount.getAccountId(),
                    false);

            if (!invitationPendingActionsRepository.existsByFromAccountAccountIdAndReceiverEmailAndGroupGroupId(
                    fromAccount.getAccountId(),
                    createInvitationPendingActionDto.getReceiverEmail(),
                    createInvitationPendingActionDto.getGroupId())) {
                invitationPendingActionEntity.setGroup(groupEntity);
            }

        }

        // Save.
        if (invitationPendingActionEntity.getSecret() != null || invitationPendingActionEntity.getGroup() != null) {
            invitationPendingActionsRepository.save(invitationPendingActionEntity);
        }

    }

    /**
     * Deletes an InvitationPendingAction associated to a secret.
     *
     * @param ownerAccountId The secret owner account ID.
     * @param secretId The secret ID.
     * @param receiverEmail The InvitationPendingAction receiver email.
     */
    public void deleteInvitationPendingActionBySecretId(
            UUID ownerAccountId,
            UUID secretId,
            String receiverEmail) {

        secretsService.findAndCheckSecret(secretId, ownerAccountId);

        invitationPendingActionsRepository.deleteBySecretSecretIdAndReceiverEmail(secretId, receiverEmail);

    }

    /**
     * Deletes an InvitationPendingAction associated to a group.
     *
     * @param ownerAccountId The group owner account ID.
     * @param groupId The group ID.
     * @param receiverEmail The InvitationPendingAction receiver email.
     */
    public void deleteInvitationPendingActionByGroupId(
            UUID ownerAccountId,
            UUID groupId,
            String receiverEmail) {

        groupsService.findAndCheckGroup(groupId, ownerAccountId, false);

        invitationPendingActionsRepository.deleteByGroupGroupIdAndReceiverEmail(groupId, receiverEmail);

    }

}
