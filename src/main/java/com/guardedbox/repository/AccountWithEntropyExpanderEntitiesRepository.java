package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithEntropyExpanderEntity;

/**
 * Repository: Account.
 * Entity: AccountWithEntropyExpanderEntity.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithEntropyExpanderEntitiesRepository
        extends JpaRepository<AccountWithEntropyExpanderEntity, Long>,
        JpaSpecificationExecutor<AccountWithEntropyExpanderEntity> {

    /**
     * @param email AccountWithEntropyExpanderEntity.email.
     * @return The AccountWithEntropyExpanderEntity corresponding to the introduced email.
     */
    AccountWithEntropyExpanderEntity findByEmail(
            String email);

}
