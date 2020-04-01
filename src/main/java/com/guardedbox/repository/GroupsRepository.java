package com.guardedbox.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.GroupEntity;

/**
 * Repository: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface GroupsRepository
        extends JpaRepository<GroupEntity, UUID>,
        JpaSpecificationExecutor<GroupEntity> {

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of GroupEntities corresponding to the introduced owner accountId.
     */
    List<GroupEntity> findByOwnerAccountAccountId(
            UUID ownerAccountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of GroupEntities in which the introduced accountId is participant.
     */
    List<GroupEntity> findByParticipantsAccountAccountId(
            UUID accountId);

}
