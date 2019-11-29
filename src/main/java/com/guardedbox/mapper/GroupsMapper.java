package com.guardedbox.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.entity.GroupEntity;

/**
 * Mapper: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
public class GroupsMapper {

    /** AccountsMapper. */
    private final AccountsMapper accountsMapper;

    /**
     * Constructor with Attributes.
     *
     * @param accountsMapper AccountsMapper.
     */
    public GroupsMapper(
            @Autowired AccountsMapper accountsMapper) {
        this.accountsMapper = accountsMapper;
    }

    /**
     * Maps a Group Entity to DTO.
     *
     * @param groupEntity The Group Entity.
     * @return The Group DTO.
     */
    public GroupDto toDto(
            GroupEntity groupEntity) {

        if (groupEntity == null)
            return null;

        GroupDto groupDto = new GroupDto();

        groupDto.setGroupId(groupEntity.getGroupId());
        groupDto.setName(groupEntity.getName());
        groupDto.setOwnerAccount(accountsMapper.toDtoWithEncryptionPublicKey(groupEntity.getOwnerAccount()));

        return groupDto;

    }

    /**
     * Maps a List of Group Entities to List of Group DTOs.
     *
     * @param groupEntities The List of Group Entities.
     * @return The List of Group DTOs.
     */
    public List<GroupDto> toDto(
            List<GroupEntity> groupEntities) {

        if (groupEntities == null)
            return null;

        List<GroupDto> groupDtos = new ArrayList<>(groupEntities.size());

        for (GroupEntity groupEntity : groupEntities)
            groupDtos.add(toDto(groupEntity));

        return groupDtos;

    }

    /**
     * Maps a Group DTO to Entity.
     *
     * @param groupDto The Group DTO.
     * @return The Group Entity.
     */
    public GroupEntity fromDto(
            CreateGroupDto groupDto) {

        if (groupDto == null)
            return null;

        GroupEntity groupEntity = new GroupEntity();

        groupEntity.setName(groupDto.getName());
        groupEntity.setEncryptedGroupKey(groupDto.getEncryptedKey());

        return groupEntity;

    }

}
