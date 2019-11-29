package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.GroupParticipantEntity;

/**
 * Repository: GroupParticipant.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface GroupParticipantEntitiesRepository
        extends JpaRepository<GroupParticipantEntity, Long>,
        JpaSpecificationExecutor<GroupParticipantEntity> {

}
