package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.SecretEntity;

import java.util.List;

/**
 * Repository: Secret.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretEntitiesRepository
        extends JpaRepository<SecretEntity, Long>,
        JpaSpecificationExecutor<SecretEntity> {

    /**
     * @param accountId Account.accountId.
     * @return The List of SecretEntities corresponding to the introduced accountId, ordered by Secret.name.
     */
    List<SecretEntity> findByAccountAccountIdOrderByNameAsc(
            Long accountId);

}
