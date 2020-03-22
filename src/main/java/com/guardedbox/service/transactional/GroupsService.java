package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.AddParticipantToGroupDto;
import com.guardedbox.dto.AddSecretToGroupDto;
import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.AccountEntity;
import com.guardedbox.entity.GroupEntity;
import com.guardedbox.entity.GroupParticipantEntity;
import com.guardedbox.entity.GroupSecretEntity;
import com.guardedbox.entity.projection.AccountBaseProjection;
import com.guardedbox.entity.projection.AccountPublicKeysProjection;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.GroupsMapper;
import com.guardedbox.repository.GroupParticipantsRepository;
import com.guardedbox.repository.GroupSecretsRepository;
import com.guardedbox.repository.GroupsRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GroupsService {

    /** GroupsRepository. */
    private final GroupsRepository groupsRepository;

    /** GroupParticipantsRepository. */
    private final GroupParticipantsRepository groupParticipantsRepository;

    /** GroupSecretsRepository. */
    private final GroupSecretsRepository groupSecretsRepository;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** GroupsMapper. */
    private final GroupsMapper groupsMapper;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of GroupDtos corresponding to the introduced owner accountId.
     */
    public List<GroupDto> getGroupsByOwnerAccountId(
            UUID ownerAccountId) {

        return groupsMapper.toDto(groupsRepository.findByOwnerAccountAccountIdOrderByNameAsc(ownerAccountId));

    }

    /**
     * @param accountId Account.accountId.
     * @return The List of GroupDtos in which the introduced accountId is participant.
     */
    public List<GroupDto> getGroupsByInvitedAccountId(
            UUID accountId) {

        List<GroupEntity> groupEntities = groupsRepository.findByParticipantsAccountAccountIdOrderByNameAsc(accountId);
        List<GroupDto> groupDtos = new ArrayList<>(groupEntities.size());

        for (GroupEntity groupEntity : groupEntities) {
            for (GroupParticipantEntity groupParticipant : groupEntity.getParticipants()) {
                if (accountId.equals(groupParticipant.getAccount().getAccountId())) {
                    AccountDto ownerAccountDto = accountsMapper.toDto(groupEntity.getOwnerAccount(AccountPublicKeysProjection.class));
                    GroupDto groupDto = groupsMapper.toDto(groupEntity)
                            .setOwnerAccount(ownerAccountDto)
                            .setEncryptedGroupKey(groupParticipant.getEncryptedGroupKey());
                    groupDtos.add(groupDto);
                    break;
                }
            }
        }

        return groupDtos;

    }

    /**
     * @param ownerOrParticipantAccountId An ID representing an account.
     * @param groupId An ID representing a group.
     * @return The list of participants in the group. Checks that the account is the owner or a participant of the group.
     */
    public List<AccountDto> getGroupParticipants(
            UUID ownerOrParticipantAccountId,
            UUID groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerOrParticipantAccountId, true);

        List<AccountDto> participants = new ArrayList<>(group.getParticipants().size());
        for (GroupParticipantEntity groupParticipant : group.getParticipants()) {
            AccountDto participant = accountsMapper.toDto(groupParticipant.getAccount(AccountPublicKeysProjection.class));
            participants.add(participant);
        }

        return participants;

    }

    /**
     * @param ownerOrParticipantAccountId An ID representing an account.
     * @param groupId An ID representing a group.
     * @return The list of secrets in the group. Checks that the account is the owner or a participant of the group.
     */
    public List<SecretDto> getGroupSecrets(
            UUID ownerOrParticipantAccountId,
            UUID groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerOrParticipantAccountId, true);

        List<SecretDto> secrets = new ArrayList<>(group.getSecrets().size());
        for (GroupSecretEntity groupSecret : group.getSecrets()) {
            SecretDto secret = new SecretDto()
                    .setSecretId(groupSecret.getGroupSecretId())
                    .setName(groupSecret.getName())
                    .setValue(groupSecret.getValue());
            secrets.add(secret);
        }

        return secrets;

    }

    /**
     * Creates a Group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param createGroupDto CreateGroupDto with the new Group data.
     * @return GroupDto with the created Group data.
     */
    public GroupDto createGroup(
            UUID ownerAccountId,
            CreateGroupDto createGroupDto) {

        GroupEntity group = groupsMapper.fromDto(createGroupDto);
        group.setOwnerAccount(new AccountEntity().setAccountId(ownerAccountId));
        return groupsMapper.toDto(groupsRepository.save(group));

    }

    /**
     * Adds a participant to a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param addParticipantToGroupDto Object with the necessary data to add a participant to a group.
     */
    public void addParticipantToGroup(
            UUID ownerAccountId,
            AddParticipantToGroupDto addParticipantToGroupDto) {

        GroupEntity group = findAndCheckGroup(addParticipantToGroupDto.getGroupId(), ownerAccountId, false);

        AccountBaseProjection account = accountsService.findAndCheckAccountByEmail(addParticipantToGroupDto.getEmail(), AccountBaseProjection.class);

        GroupParticipantEntity groupParticipant = new GroupParticipantEntity()
                .setGroup(group)
                .setAccount(new AccountEntity().setAccountId(account.getAccountId()))
                .setEncryptedGroupKey(addParticipantToGroupDto.getEncryptedGroupKey());
        groupParticipantsRepository.save(groupParticipant);

    }

    /**
     * Adds a secret to a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param addSecretToGroupDto Object with the necessary data to add a secret to a group.
     * @return SecretDto with the stored secret data.
     */
    public SecretDto addSecretToGroup(
            UUID ownerAccountId,
            AddSecretToGroupDto addSecretToGroupDto) {

        GroupEntity group = findAndCheckGroup(addSecretToGroupDto.getGroupId(), ownerAccountId, false);

        GroupSecretEntity groupSecret = new GroupSecretEntity()
                .setGroup(group)
                .setName(addSecretToGroupDto.getName())
                .setValue(addSecretToGroupDto.getValue());
        groupSecret = groupSecretsRepository.save(groupSecret);

        SecretDto secret = new SecretDto()
                .setSecretId(groupSecret.getGroupSecretId())
                .setName(groupSecret.getName())
                .setValue(groupSecret.getValue());
        return secret;

    }

    /**
     * Deletes a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param groupId Group.groupId of the group.
     * @return GroupDto with the deleted group data.
     */
    public GroupDto deleteGroup(
            UUID ownerAccountId,
            UUID groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerAccountId, false);
        groupsRepository.delete(group);
        return groupsMapper.toDto(group);

    }

    /**
     * Removes a participant from a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param groupId Group.groupId of the group.
     * @param email Email of the participant to be removed from the group.
     */
    public void removeParticipantFromGroup(
            UUID ownerAccountId,
            UUID groupId,
            String email) {

        GroupEntity group = findAndCheckGroup(groupId, ownerAccountId, false);

        for (int i = 0; i < group.getParticipants().size(); i++) {
            GroupParticipantEntity participant = group.getParticipants().get(i);
            if (email.equals(participant.getAccount(AccountBaseProjection.class).getEmail())) {
                group.getParticipants().remove(i);
            }
        }

    }

    /**
     * Deletes a secret from a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param groupId Group.groupId of the group.
     * @param groupSecretId GroupSecret.groupSecretId.
     */
    public void deleteSecretFromGroup(
            UUID ownerAccountId,
            UUID groupId,
            UUID groupSecretId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerAccountId, false);

        for (int i = 0; i < group.getSecrets().size(); i++) {
            GroupSecretEntity secret = group.getSecrets().get(i);
            if (groupSecretId.equals(secret.getGroupSecretId())) {
                group.getSecrets().remove(i);
            }
        }

    }

    /**
     * Finds a Group by groupId and checks if it exists and belongs to an accountId.
     *
     * @param groupId The groupId.
     * @param accountId The accountId.
     * @param participantAllowed If this parameter is set to true, the method will check if the accountId owns the group or is a participant.
     * @return The Group.
     */
    protected GroupEntity findAndCheckGroup(
            UUID groupId,
            UUID accountId,
            boolean participantAllowed) {

        GroupEntity group = groupsRepository.findById(groupId).orElse(null);

        if (group == null) {
            throw new ServiceException(String.format("Group %s does not exist", groupId))
                    .setErrorCode("groups.group-does-not-exist");
        }

        if (accountId != null) {
            if (!accountId.equals(group.getOwnerAccount().getAccountId())) {
                boolean allowedBecauseParticipant = false;
                if (participantAllowed) {
                    for (GroupParticipantEntity participant : group.getParticipants()) {
                        if (accountId.equals(participant.getAccount().getAccountId())) {
                            allowedBecauseParticipant = true;
                            break;
                        }
                    }
                }
                if (!allowedBecauseParticipant) {
                    throw new AuthorizationServiceException(String.format(
                            "Group %s cannot be managed by account %s", groupId, accountId));
                }
            }
        }

        return group;

    }

}
