package com.guardedbox.service.transactional;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.entity.AccountWithEncryptionPublicKeyEntity;
import com.guardedbox.entity.GroupEntity;
import com.guardedbox.mapper.GroupsMapper;
import com.guardedbox.repository.GroupEntitiesRepository;

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

    /** GroupsMapper. */
    private final GroupsMapper groupsMapper;

    /**
     * Constructor with Attributes.
     *
     * @param groupEntitiesRepository GroupEntitiesRepository.
     * @param groupsMapper GroupsMapper.
     */
    public GroupsService(
            @Autowired GroupEntitiesRepository groupEntitiesRepository,
            @Autowired GroupsMapper groupsMapper) {
        this.groupEntitiesRepository = groupEntitiesRepository;
        this.groupsMapper = groupsMapper;
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

}
