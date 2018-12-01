package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithPublicKeyEntity;

/**
 * Repository: Account.
 * Entity: AccountWithPublicKeyEntity.
 * 
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithPublicKeyEntitiesRepository
        extends JpaRepository<AccountWithPublicKeyEntity, Long>,
        JpaSpecificationExecutor<AccountWithPublicKeyEntity> {

    /**
     * @param email AccountWithPublicKeyEntity.email.
     * @return The AccountWithPublicKeyEntity corresponding to the introduced email.
     */
    AccountWithPublicKeyEntity findByEmail(
            String email);

}
