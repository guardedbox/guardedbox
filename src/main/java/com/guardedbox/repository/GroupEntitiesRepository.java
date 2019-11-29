package com.guardedbox.repository;

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

}
