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

}
