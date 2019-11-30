package com.guardedbox.service.transactional;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.AccountWithEncryptionPublicKeyDto;
import com.guardedbox.dto.AddParticipantToGroupDto;
import com.guardedbox.dto.AddSecretToGroupDto;
import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.dto.SecretDto;
import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;
import com.guardedbox.entity.GroupEntity;
import com.guardedbox.entity.GroupParticipantEntity;
import com.guardedbox.entity.GroupSecretEntity;
import com.guardedbox.exception.ServiceException;
import com.guardedbox.mapper.AccountsMapper;
import com.guardedbox.mapper.GroupsMapper;
import com.guardedbox.repository.GroupEntitiesRepository;
import com.guardedbox.repository.GroupParticipantEntitiesRepository;
import com.guardedbox.repository.GroupSecretEntitiesRepository;

/**
 * Service: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@Transactional
public class GroupsService {

    /** GroupEntitiesRepository. */
    private final GroupEntitiesRepository groupEntitiesRepository;

    /** GroupParticipantEntitiesRepository. */
    private final GroupParticipantEntitiesRepository groupParticipantEntitiesRepository;

    /** GroupSecretEntitiesRepository. */
    private final GroupSecretEntitiesRepository groupSecretEntitiesRepository;

    /** GroupsMapper. */
    private final GroupsMapper groupsMapper;

    /** AccountsService. */
    private final AccountsService accountsService;

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * Constructor with Attributes.
     *
     * @param groupEntitiesRepository GroupEntitiesRepository.
     * @param groupParticipantEntitiesRepository GroupParticipantEntitiesRepository.
     * @param groupSecretEntitiesRepository GroupSecretEntitiesRepository.
     * @param groupsMapper GroupsMapper.
     * @param accountsService AccountsService.
     * @param accountsMapper AccountsMapper.
     */
    public GroupsService(
            @Autowired GroupEntitiesRepository groupEntitiesRepository,
            @Autowired GroupParticipantEntitiesRepository groupParticipantEntitiesRepository,
            @Autowired GroupSecretEntitiesRepository groupSecretEntitiesRepository,
            @Autowired GroupsMapper groupsMapper,
            @Autowired AccountsService accountsService,
            @Autowired AccountsMapper accountsMapper) {
        this.groupEntitiesRepository = groupEntitiesRepository;
        this.groupParticipantEntitiesRepository = groupParticipantEntitiesRepository;
        this.groupSecretEntitiesRepository = groupSecretEntitiesRepository;
        this.groupsMapper = groupsMapper;
        this.accountsService = accountsService;
        this.accountsMapper = accountsMapper;
    }

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of GroupDtos corresponding to the introduced owner accountId.
     */
    public List<GroupDto> getGroupsByOwnerAccountId(
            Long ownerAccountId) {

        return groupsMapper.toDto(groupEntitiesRepository.findByOwnerAccountAccountIdOrderByNameAsc(ownerAccountId));

    }

    /**
     * @param accountId Account.accountId.
     * @return The List of GroupDtos in which the introduced accountId is participant.
     */
    public List<GroupDto> getGroupsByInvitedAccountId(
            Long accountId) {

        List<GroupEntity> groupEntities = groupEntitiesRepository.findByParticipantsAccountAccountIdOrderByNameAsc(accountId);
        List<GroupDto> groupDtos = new ArrayList<>(groupEntities.size());

        for (GroupEntity groupEntity : groupEntities) {
            for (GroupParticipantEntity groupParticipant : groupEntity.getParticipants()) {
                if (accountId.equals(groupParticipant.getAccount().getAccountId())) {
                    GroupDto groupDto = groupsMapper.toDto(groupEntity);
                    groupDto.setEncryptedGroupKey(groupParticipant.getEncryptedGroupKey());
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
    public List<AccountWithEncryptionPublicKeyDto> getGroupParticipants(
            Long ownerOrParticipantAccountId,
            Long groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerOrParticipantAccountId, true);

        List<AccountWithEncryptionPublicKeyDto> participants = new ArrayList<>(group.getParticipants().size());
        for (GroupParticipantEntity groupParticipant : group.getParticipants()) {
            participants.add(accountsMapper.toDtoWithEncryptionPublicKey(groupParticipant.getAccount()));
        }

        return participants;

    }

    /**
     * @param ownerOrParticipantAccountId An ID representing an account.
     * @param groupId An ID representing a group.
     * @return The list of secrets in the group. Checks that the account is the owner or a participant of the group.
     */
    public List<SecretDto> getGroupSecrets(
            Long ownerOrParticipantAccountId,
            Long groupId) {

        GroupEntity group = findAndCheckGroup(groupId, ownerOrParticipantAccountId, true);

        List<SecretDto> secrets = new ArrayList<>(group.getSecrets().size());
        for (GroupSecretEntity groupSecret : group.getSecrets()) {
            SecretDto secret = new SecretDto();
            secret.setSecretId(groupSecret.getGroupSecretId());
            secret.setName(groupSecret.getName());
            secret.setValue(groupSecret.getValue());
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
            Long ownerAccountId,
            @Valid CreateGroupDto createGroupDto) {

        GroupEntity group = groupsMapper.fromDto(createGroupDto);
        group.setOwnerAccount(new AccountWithEncryptionPublicKeyEntity(ownerAccountId));
        return groupsMapper.toDto(groupEntitiesRepository.save(group));

    }

    /**
     * Adds a participant to a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param addParticipantToGroupDto Object with the necessary data to add a participant to a group.
     */
    public void addParticipantToGroup(
            Long ownerAccountId,
            AddParticipantToGroupDto addParticipantToGroupDto) {

        GroupEntity group = findAndCheckGroup(addParticipantToGroupDto.getGroupId(), ownerAccountId, false);

        AccountWithEncryptionPublicKeyEntity account =
                accountsService.findAndCheckAccountWithEncryptionPublicKeyByEmail(addParticipantToGroupDto.getEmail());

        GroupParticipantEntity groupParticipant = new GroupParticipantEntity();
        groupParticipant.setGroup(group);
        groupParticipant.setAccount(account);
        groupParticipant.setEncryptedGroupKey(addParticipantToGroupDto.getEncryptedGroupKey());
        groupParticipantEntitiesRepository.save(groupParticipant);

    }

    /**
     * Adds a secret to a group.
     *
     * @param ownerAccountId Account.accountId of the group owner.
     * @param addSecretToGroupDto Object with the necessary data to add a secret to a group.
     * @return SecretDto with the stored secret data.
     */
    public SecretDto addSecretToGroup(
            Long ownerAccountId,
            AddSecretToGroupDto addSecretToGroupDto) {

        GroupEntity group = findAndCheckGroup(addSecretToGroupDto.getGroupId(), ownerAccountId, false);

        GroupSecretEntity groupSecret = new GroupSecretEntity();
        groupSecret.setGroup(group);
        groupSecret.setName(addSecretToGroupDto.getName());
        groupSecret.setValue(addSecretToGroupDto.getValue());
        groupSecret = groupSecretEntitiesRepository.save(groupSecret);

        SecretDto secret = new SecretDto();
        secret.setSecretId(groupSecret.getGroupSecretId());
        secret.setName(groupSecret.getName());
        secret.setValue(groupSecret.getValue());
        return secret;

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
            Long groupId,
            Long accountId,
            boolean participantAllowed) {

        GroupEntity group = groupEntitiesRepository.findById(groupId).orElse(null);

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
