package com.guardedbox.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.guardedbox.dto.CreateGroupDto;
import com.guardedbox.dto.GroupDto;
import com.guardedbox.entity.GroupEntity;

import lombok.RequiredArgsConstructor;

/**
 * Mapper: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
@Service
@RequiredArgsConstructor
public class GroupsMapper {

    /**
     * Maps a Group Entity to DTO.
     *
     * @param groupEntity The Group Entity.
     * @return The Group DTO.
     */
    public GroupDto toDto(
            GroupEntity groupEntity) {

        return groupEntity == null ? null : new GroupDto()
                .setGroupId(groupEntity.getGroupId())
                .setName(groupEntity.getName())
                .setEncryptedGroupKey(groupEntity.getEncryptedGroupKey());

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

        return groupDto == null ? null : new GroupEntity()
                .setName(groupDto.getName())
                .setEncryptedGroupKey(groupDto.getEncryptedGroupKey());

    }

}
