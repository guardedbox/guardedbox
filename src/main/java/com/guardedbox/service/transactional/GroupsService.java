package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountDto;
import com.guardedbox.dto.AddParticipantToGroupDto;
import com.guardedbox.dto.AddSecretToGroupDto;
import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.EditGroupDto;
import com.guardedbox.dto.EditGroupEditSecretDto;
import com.guardedbox.dto.EditGroupSecretDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.dto.ShareSecretDto;
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
     * @param ownerOrParticipantAccountId An ID representing an account.
     * @param groupId An ID representing a group.
     * @return The GroupDto corresponding to the introduced groupId. Checks that the account is the owner or a participant of the group.
     */
    public GroupDto getGroup(
            UUID ownerOrParticipantAccountId,
            UUID groupId) {

        GroupEntity groupEntity = findAndCheckGroup(groupId, ownerOrParticipantAccountId, true);
        GroupDto groupDto = groupsMapper.toDto(groupEntity);

        groupDto.setSecrets(new ArrayList<>(groupEntity.getSecrets().size()));
        for (GroupSecretEntity groupSecret : groupEntity.getSecrets()) {
            SecretDto secret = new SecretDto()
                    .setSecretId(groupSecret.getGroupSecretId())
                    .setValue(groupSecret.getValue());
            groupDto.getSecrets().add(secret);
        }

        return groupDto;

    }

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of GroupDtos corresponding to the introduced owner accountId.
     */
    public List<GroupDto> getGroupsByOwnerAccountId(
            UUID ownerAccountId) {

        List<GroupEntity> groupEntities = groupsRepository.findByOwnerAccountAccountId(ownerAccountId);
        List<GroupDto> groupDtos = new ArrayList<>(groupEntities.size());

        for (GroupEntity groupEntity : groupEntities) {

            GroupDto groupDto = groupsMapper.toDto(groupEntity);

            groupDto.setSecrets(new ArrayList<>(groupEntity.getSecrets().size()));
            for (GroupSecretEntity groupSecret : groupEntity.getSecrets()) {
                SecretDto secret = new SecretDto()
                        .setSecretId(groupSecret.getGroupSecretId())
                        .setValue(groupSecret.getValue());
                groupDto.getSecrets().add(secret);
            }

            groupDtos.add(groupDto);

        }

        return groupDtos;

    }

    /**
     * @param accountId Account.accountId.
     * @return The List of GroupDtos in which the introduced accountId is participant.
     */
    public List<GroupDto> getGroupsByParticipantAccountId(
            UUID accountId) {

        List<GroupEntity> groupEntities = groupsRepository.findByParticipantsAccountAccountId(accountId);
        List<GroupDto> groupDtos = new ArrayList<>(groupEntities.size());

        for (GroupEntity groupEntity : groupEntities) {

            for (GroupParticipantEntity groupParticipant : groupEntity.getParticipants()) {

                if (accountId.equals(groupParticipant.getAccount().getAccountId())) {

                    AccountDto ownerAccountDto = accountsMapper.toDto(groupEntity.getOwnerAccount(AccountPublicKeysProjection.class));

                    GroupDto groupDto = groupsMapper.toDto(groupEntity)
                            .setOwnerAccount(ownerAccountDto)
                            .setEncryptedKey(groupParticipant.getEncryptedKey())
                            .setSecrets(new ArrayList<>(groupEntity.getSecrets().size()));

                    for (GroupSecretEntity groupSecret : groupEntity.getSecrets()) {
                        SecretDto secret = new SecretDto()
                                .setSecretId(groupSecret.getGroupSecretId())
                                .setValue(groupSecret.getValue());
                        groupDto.getSecrets().add(secret);
                    }

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
     * @param ownerAccountId Account.accountId.
     * @param groupId Group.groupId.
     * @return GroupDto indicating if the group corresponding to the introduced groupId must rotate its key.
     */
    public GroupDto getGroupMustRotateKey(
            UUID ownerAccountId,
            UUID groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerAccountId, false);

        return new GroupDto()
                .setGroupId(group.getGroupId())
                .setMustRotateKey(group.getMustRotateKey());

    }

    /**
     * Creates a Group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param createGroupDto Object with the new Group data.
     * @return GroupDto with the created Group data.
     */
    public GroupDto createGroup(
            UUID ownerAccountId,
            CreateGroupDto createGroupDto) {

        GroupEntity group = groupsMapper.fromDto(createGroupDto)
                .setOwnerAccount(new AccountEntity().setAccountId(ownerAccountId))
                .setMustRotateKey(false)
                .setHadParticipants(false);

        return groupsMapper.toDto(groupsRepository.save(group));

    }

    /**
     * Edits a Group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param editGroupDto Object with the necessary data to edit the Group.
     * @return GroupDto with the edited Group data.
     */
    public GroupDto editGroup(
            UUID ownerAccountId,
            EditGroupDto editGroupDto) {

        GroupEntity group = findAndCheckGroup(editGroupDto.getGroupId(), ownerAccountId, false);

        if (group.getMustRotateKey() && (editGroupDto.getSecrets() == null || editGroupDto.getParticipants() == null)) {
            throw new ServiceException(String.format(
                    "Group %s must rotate key", editGroupDto.getGroupId()));
        }

        if (editGroupDto.getParticipants() != null) {
            if (editGroupDto.getParticipants().size() != group.getParticipants().size()) {
                throw new ServiceException(String.format(
                        "Edit group participants do not match group %s current participants", editGroupDto.getGroupId()));
            }
            Map<String, ShareSecretDto> editGroupParticipants = new HashMap<>();
            for (ShareSecretDto editGroupParticipant : editGroupDto.getParticipants()) {
                editGroupParticipants.put(editGroupParticipant.getEmail(), editGroupParticipant);
            }
            for (GroupParticipantEntity participant : group.getParticipants()) {
                ShareSecretDto editGroupParticipant = editGroupParticipants.get(
                        participant.getAccount(AccountBaseProjection.class).getEmail());
                if (editGroupParticipant == null) {
                    throw new ServiceException(String.format(
                            "Edit group participants do not match group %s current participants", editGroupDto.getGroupId()));
                }
                participant.setEncryptedKey(editGroupParticipant.getEncryptedKey());
            }
        }

        if (editGroupDto.getSecrets() != null) {
            if (editGroupDto.getSecrets().size() != group.getSecrets().size()) {
                throw new ServiceException(String.format(
                        "Edit group secrets do not match group %s current secrets", editGroupDto.getGroupId()));
            }
            Map<UUID, EditGroupEditSecretDto> editGroupSecrets = new HashMap<>();
            for (EditGroupEditSecretDto editGroupSecret : editGroupDto.getSecrets()) {
                editGroupSecrets.put(editGroupSecret.getSecretId(), editGroupSecret);
            }
            for (GroupSecretEntity groupSecret : group.getSecrets()) {
                EditGroupEditSecretDto editGroupSecret = editGroupSecrets.get(groupSecret.getGroupSecretId());
                if (editGroupSecret == null) {
                    throw new ServiceException(String.format(
                            "Edit group secrets do not match group %s current secrets", editGroupDto.getGroupId()));
                }
                groupSecret.setValue(editGroupSecret.getValue());
            }
        }

        group
                .setName(editGroupDto.getName())
                .setEncryptedKey(editGroupDto.getEncryptedKey())
                .setMustRotateKey(false);

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
        groupsRepository.save(group.setHadParticipants(true));

        AccountBaseProjection account = accountsService.findAndCheckAccountByEmail(addParticipantToGroupDto.getEmail(), AccountBaseProjection.class);
        if (account.getAccountId().equals(ownerAccountId)) {
            throw new ServiceException(String.format(
                    "Group %s belongs to email %s", addParticipantToGroupDto.getGroupId(), addParticipantToGroupDto.getEmail()))
                            .setErrorCode("groups.do-not-self-invite");
        }

        for (GroupParticipantEntity participant : group.getParticipants()) {
            if (participant.getAccount().getAccountId().equals(account.getAccountId())) {
                throw new ServiceException(String.format(
                        "Email %s is already participant in group %s", addParticipantToGroupDto.getEmail(), addParticipantToGroupDto.getGroupId()))
                                .setErrorCode("groups.email-already-participant-in-group")
                                .addAdditionalData("email", addParticipantToGroupDto.getEmail());
            }
        }

        GroupParticipantEntity groupParticipant = new GroupParticipantEntity()
                .setGroup(group)
                .setAccount(new AccountEntity().setAccountId(account.getAccountId()))
                .setEncryptedKey(addParticipantToGroupDto.getEncryptedKey());

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

        if (group.getMustRotateKey()) {
            throw new ServiceException(String.format(
                    "Group %s must rotate key", addSecretToGroupDto.getGroupId()));
        }

        GroupSecretEntity groupSecret = new GroupSecretEntity()
                .setGroup(group)
                .setValue(addSecretToGroupDto.getValue());
        groupSecret = groupSecretsRepository.save(groupSecret);

        return new SecretDto()
                .setSecretId(groupSecret.getGroupSecretId())
                .setValue(groupSecret.getValue());

    }

    /**
     * Edits a group secret.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param editGroupSecretDto editGroupSecretDto Object with the necessary data to edit the group secret.
     * @return SecretDto with the edited secret data.
     */
    public SecretDto editGroupSecret(
            UUID ownerAccountId,
            EditGroupSecretDto editGroupSecretDto) {

        GroupEntity group = findAndCheckGroup(editGroupSecretDto.getGroupId(), ownerAccountId, false);

        if (group.getMustRotateKey()) {
            throw new ServiceException(String.format(
                    "Group %s must rotate key", editGroupSecretDto.getGroupId()));
        }

        for (GroupSecretEntity groupSecret : group.getSecrets()) {
            if (groupSecret.getGroupSecretId().equals(editGroupSecretDto.getSecretId())) {
                groupSecret.setValue(editGroupSecretDto.getValue());
            }
        }

        groupsRepository.save(group);

        return new SecretDto()
                .setSecretId(editGroupSecretDto.getSecretId())
                .setValue(editGroupSecretDto.getValue());

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
        groupsRepository.save(group.setMustRotateKey(true));

        for (int i = 0; i < group.getParticipants().size(); i++) {
            GroupParticipantEntity participant = group.getParticipants().get(i);
            if (email.equals(participant.getAccount(AccountBaseProjection.class).getEmail())) {
                group.getParticipants().remove(i);
            }
        }

    }

    /**
     * Removes a participant from a group.
     *
     * @param groupId Group.groupId of the group.
     * @param participantAccountId Account.accountId of the participant.
     */
    public void exitFromGroup(
            UUID groupId,
            UUID participantAccountId) {

        GroupEntity group = findAndCheckGroup(groupId, null, false);
        groupsRepository.save(group.setMustRotateKey(true));

        GroupParticipantEntity groupParticipant = groupParticipantsRepository.findByGroupGroupIdAndAccountAccountId(groupId, participantAccountId);
        if (groupParticipant == null) {
            throw new ServiceException(String.format(
                    "Account %s is not a participant of the group %s", participantAccountId, groupId))
                            .setErrorCode("groups-i-was-added-to.you-are-not-participant-in-group");
        }

        groupParticipantsRepository.delete(groupParticipant);

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
