package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.GroupParticipantEntity;

/**
 * Repository: GroupParticipant.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface GroupParticipantsRepository
        extends JpaRepository<GroupParticipantEntity, UUID>,
        JpaSpecificationExecutor<GroupParticipantEntity> {

    /**
     * @param groupId Group.groupId.
     * @param participantAccountId Account.accountId.
     * @return The GroupParticipantEntity corresponding to the introduced groupId and accountId.
     */
    GroupParticipantEntity findByGroupGroupIdAndAccountAccountId(
            UUID groupId,
            UUID participantAccountId);

}
