package com.guardedbox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.GroupEntity;

/**
 * Repository: Group.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface GroupEntitiesRepository
        extends JpaRepository<GroupEntity, Long>,
        JpaSpecificationExecutor<GroupEntity> {

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of GroupEntities corresponding to the introduced owner accountId, ordered by Group.name.
     */
    List<GroupEntity> findByOwnerAccountAccountIdOrderByNameAsc(
            Long ownerAccountId);

    /**
     * @param accountId Account.accountId.
     * @return The List of GroupEntities in which the introduced accountId is participant, ordered by Group.name.
     */
    List<GroupEntity> findByParticipantsAccountAccountIdOrderByNameAsc(
            Long accountId);

}
