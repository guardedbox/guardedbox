package com.guardedbox.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithSaltEntity;

/**
 * Repository: Account.
 * Entity: AccountWithSaltEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithSaltEntitiesRepository
        extends JpaRepository<AccountWithSaltEntity, UUID>,
        JpaSpecificationExecutor<AccountWithSaltEntity> {

    /**
     * @param email AccountWithSaltEntity.email.
     * @return The AccountWithSaltEntity corresponding to the introduced email.
     */
    AccountWithSaltEntity findByEmail(
            String email);

}
