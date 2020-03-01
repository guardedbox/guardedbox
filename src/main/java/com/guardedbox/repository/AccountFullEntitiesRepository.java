package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountFullEntity;

/**
 * Repository: Account.
 * Entity: AccountFullEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountFullEntitiesRepository
        extends JpaRepository<AccountFullEntity, UUID>,
        JpaSpecificationExecutor<AccountFullEntity> {

    /**
     * @param email AccountFullEntity.email.
     * @return Boolean indicating if an AccountFullEntity corresponding to the introduced email exists.
     */
    boolean existsByEmail(
            String email);

    /**
     * @param salt AccountFullEntity.salt.
     * @return Boolean indicating if an AccountFullEntity corresponding to the introduced salt exists.
     */
    boolean existsBySalt(
            String salt);

}
