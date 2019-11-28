package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountEntity;

/**
 * Repository: Account.
 * Entity: AccountEntity.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountEntitiesRepository
        extends JpaRepository<AccountEntity, Long>,
        JpaSpecificationExecutor<AccountEntity> {

    /**
     * @param email AccountEntity.email.
     * @return The AccountEntity corresponding to the introduced email.
     */
    AccountEntity findByEmail(
            String email);

}
