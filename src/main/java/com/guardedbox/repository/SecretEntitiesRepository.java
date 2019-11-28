package com.guardedbox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.SecretEntity;

/**
 * Repository: Secret.
 * Entity: SecretEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface SecretEntitiesRepository
        extends JpaRepository<SecretEntity, Long>,
        JpaSpecificationExecutor<SecretEntity> {

    /**
     * @param ownerAccountId Account.accountId.
     * @return The List of SecretEntities corresponding to the introduced owner accountId, ordered by Secret.name.
     */
    List<SecretEntity> findByOwnerAccountAccountIdOrderByNameAsc(
            Long ownerAccountId);

}
