package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithPasswordEntity;

/**
 * Repository: Account.
 * Entity: AccountWithPasswordEntity.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithPasswordEntitiesRepository
        extends JpaRepository<AccountWithPasswordEntity, Long>,
        JpaSpecificationExecutor<AccountWithPasswordEntity> {

    /**
     * @param email AccountWithPasswordEntity.email.
     * @return The AccountWithPasswordEntity corresponding to the introduced email.
     */
    AccountWithPasswordEntity findByEmail(
            String email);

}
