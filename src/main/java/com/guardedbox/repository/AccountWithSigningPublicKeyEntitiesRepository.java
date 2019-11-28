package com.guardedbox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.guardedbox.entity.AccountWithSigningPublicKeyEntity;

/**
 * Repository: Account.
 * Entity: AccountWithSigningPublicKeyEntity.
 *
 * @author s3curitybug@gmail.com
 *
 */
public interface AccountWithSigningPublicKeyEntitiesRepository
        extends JpaRepository<AccountWithSigningPublicKeyEntity, Long>,
        JpaSpecificationExecutor<AccountWithSigningPublicKeyEntity> {

    /**
     * @param email AccountWithSigningPublicKeyEntity.email.
     * @return The AccountWithSigningPublicKeyEntity corresponding to the introduced email.
     */
    AccountWithSigningPublicKeyEntity findByEmail(
            String email);

}
